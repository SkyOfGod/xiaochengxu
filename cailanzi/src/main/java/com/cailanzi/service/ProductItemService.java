package com.cailanzi.service;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.ProductJdMapper;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.ProductStatusMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.ProductStatus;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.util.Date;
import java.util.List;

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

    public EasyUIResult queryJdProductPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());
        ProductJd product = new ProductJd();
        if(StringUtils.isNotBlank(productListInput.getSkuId())){
            product.setSkuId(Long.parseLong(productListInput.getSkuId()));
        }
        if(StringUtils.isNotBlank(productListInput.getFixedStatus())){
            product.setFixedStatus(Byte.parseByte(productListInput.getFixedStatus()));
        }
        if(StringUtils.isNotBlank(productListInput.getSkuName())){
            product.setSkuName(productListInput.getSkuName());
        }
        List<ProductJd> list = productJdMapper.select(product);
        log.info("ProductItemService queryJdProductPage list={}", list);
        PageInfo<ProductJd> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult queryProductPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());
        List<Product> list = null;
        if(StringUtils.isNotBlank(productListInput.getPhone())){
            Product product = new Product();
            product.setPhone(productListInput.getPhone());
            list = productMapper.select(product);
        }else {
            list = productMapper.selectAll();
        }
        log.info("ProductItemService queryProductPage list={}", list);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult queryProductStatusPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());

        ProductStatus product = new ProductStatus();
        product.setCreateTime(null);
        if(StringUtils.isNotBlank(productListInput.getStationNo())){
            product.setStationNo(productListInput.getStationNo());
        }
        if(StringUtils.isNotBlank(productListInput.getVendibility())){
            product.setVendibility(Byte.parseByte(productListInput.getVendibility()));
        }
        List<ProductStatus> list = productStatusMapper.select(product);
        log.info("ProductItemService queryProductStatusPage list={}", list);
        PageInfo<ProductStatus> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public List<ProductJd> comgridJdList(String q,String belongStationNo) {
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
        int count = productMapper.insertSelective(product);
        log.info("ProductItemService addProduct insertSelective successSum={}, id={}", count,product.getId());
    }


    public void updateProduct(Product product) throws Exception{
        product.setUpdateTime(new Date());
        log.info("ProductItemService updateProduct product={}", product);
        productMapper.updateByPrimaryKeySelective(product);
    }

    public void deleteProduct(String ids) throws Exception{
        log.info("ProductItemService deleteProduct ids={}", ids);
        String[] arr = ids.split(",");
        for (String id : arr) {
            productMapper.deleteByPrimaryKey(id);
        }
    }

    public void updateProductStatus(ProductStatus productStatus) throws Exception{
        log.info("ProductItemService updateProduct productStatus={}", productStatus);
        /*ProductStatus tempProductStatus = new ProductStatus();
        tempProductStatus.setStationNo(productStatus.getStationNo());
        tempProductStatus.setSkuId(productStatus.getSkuId());
        tempProductStatus.setCreateTime(null);

        ProductStatus oldProductStatus = productStatusMapper.selectOne(tempProductStatus);
        if(oldProductStatus.getUsableQty()==productStatus.getUsableQty()
                &&oldProductStatus.getVendibility()==productStatus.getVendibility()){
            return;
        }*/
        productStatusMapper.updateStoreAndStatus(productStatus);
    }
}
