package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.ProductCategoryMapper;
import com.cailanzi.mapper.ProductJdMapper;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.ProductStatusMapper;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ProductCategory;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.ProductStatus;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by v-hel27 on 2018/8/12.
 */
@Slf4j
@Service
public class ProductItemService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductJdMapper productJdMapper;
    @Autowired
    private ProductStatusMapper productStatusMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    @Autowired
    private ProductService productService;

    public EasyUIResult queryJdProductPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());
        List<ProductJd> list = productJdMapper.selectDynamic(productListInput);
        log.info("ProductItemService queryJdProductPage list={}", list);
        PageInfo<ProductJd> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult queryProductPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());
        List<Product> list = productMapper.selectDynamic(productListInput);
        log.info("ProductItemService queryProductPage list={}", list);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public List<ProductJd> comgridJdList(String q, String belongStationNo) {
        log.info("ProductItemService comgridJdList q={}", q);
        List<ProductJd> list = productJdMapper.comgridJdList(q,belongStationNo);
        log.info("ProductItemService comgridJdList return list={}", list);
        return list;
    }

    public void addProduct(Product product) throws ServiceException{
        Product product1 = new Product();
        product1.setPhone(product.getPhone());
        product1.setSkuId(product.getSkuId());
        List<Product> list = productMapper.select(product1);
        if(!list.isEmpty()){
            throw new ServiceException("该商户下已经拥有此商品！");
        }

        product.setCreateTime(new Date());
        product.setUpdateTime(product.getCreateTime());
        log.info("ProductItemService addProduct product={}", product);
        productMapper.insertSelective(product);
    }

    /*public void updateProduct(Product product) throws Exception{
        product.setUpdateTime(new Date());
        productMapper.updateByPrimaryKeySelective(product);
    }*/

    /**
     * 修改商户商品 库存/价格/可售状态  根据比率触发京东商品
     * @param product
     */
    @Transactional
    public void updateProductOfStorePriceVendibility(Product product) throws Exception{
        product.setUpdateTime(new Date());
        log.info("ProductItemService updateProductOfStorePriceVendibility product={}", product);

        ProductStatus productStatus = new ProductStatus();
        productStatus.setStationNo(product.getBelongStationNo());
        productStatus.setSkuId(product.getSkuId());
        if(product.getPrice()!=null){
            Integer rate = productMapper.getRateBySkuId(product.getSkuId());
            if(rate==null){
                throw new ServiceException("未设置商品对应的比率");
            }
            Integer ratePrice = product.getPrice()*rate/100;
            productStatus.setPrice(ratePrice);
        }
        if(product.getStoreNum()!=null){
            productStatus.setCurrentQty(product.getStoreNum());
        }
        if(product.getIsSell()!=null){
            productStatus.setVendibility(product.getIsSell());
        }
        productMapper.updateProductOfStorePriceVendibility(product);
        updateProductStatusOfStorePriceVendibility(productStatus);
    }

    public void deleteProduct(String ids) throws Exception{
        String[] arr = ids.split(",");
        for (String id : arr) {
            productMapper.deleteByPrimaryKey(id);
        }
    }

    /**************************** productCategorie ********************************/
    public EasyUIResult categoriesPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());

        ProductCategory productCategory = new ProductCategory();
        if(StringUtils.isNotBlank(productListInput.getCategoryPid())){
            productCategory.setPid(productListInput.getCategoryPid());
        }
        List<ProductCategory> list = productCategoryMapper.select(productCategory);
        log.info("ProductItemService categoriesPage list={}", list);
        PageInfo<ProductCategory> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /****************************** ProductStatus **************************************/
    public EasyUIResult queryProductStatusPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());
        List<ProductStatus> list = productStatusMapper.selectDynamic(productListInput);
        log.info("ProductItemService queryProductStatusPage list={}", list);
        PageInfo<ProductStatus> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 修改京东商品 库存/价格/可售状态 触发京东接口修改
     * @param productStatus
     * @throws Exception
     */
    @Transactional
    public void updateProductStatusOfStorePriceVendibility(ProductStatus productStatus) throws Exception {
        log.info("ProductItemService updateProduct productStatus={}", productStatus);
        //修改京东价格
        Long skuId = productStatus.getSkuId();
        String stationNo = productStatus.getStationNo();
        if(productStatus.getPrice()!=null){
            productService.updateProductPrice(skuId,stationNo,productStatus.getPrice());
        }
        //修改京东现货库存
        if(productStatus.getCurrentQty()!=null){
            productService.updateProductStore(stationNo,skuId,productStatus.getCurrentQty());
        }
        //修改京东可售状态
        if(productStatus.getVendibility()!=null){
            productService.updateProductVendibility(stationNo,skuId,productStatus.getVendibility());
        }
        productStatusMapper.updateProductStatusOfStorePriceVendibility(productStatus);
    }

    public void updateProductImg(ProductJd productjd) {
        productJdMapper.updateByPrimaryKeySelective(productjd);
    }
}
