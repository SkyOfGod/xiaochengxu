package com.cailanzi.service;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.ProductCategoryMapper;
import com.cailanzi.mapper.ProductJdMapper;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.ProductStatusMapper;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.*;
import com.cailanzi.utils.ConstantsUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
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
        List<Product> list = productMapper.selectDynamic(productListInput);
        log.info("ProductItemService queryProductPage list={}", list);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
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
        productMapper.insertSelective(product);
    }


    public void updateProduct(Product product) throws Exception{
        product.setUpdateTime(new Date());
        productMapper.updateByPrimaryKeySelective(product);
    }

    private void updateProductOfStorePriceVendibility(Product product) {
        product.setUpdateTime(new Date());
        log.info("ProductItemService updateProductOfStorePriceVendibility product={}", product);
        productMapper.updateProductOfStorePriceVendibility(product);
    }

    public void deleteProduct(String ids) throws Exception{
        String[] arr = ids.split(",");
        for (String id : arr) {
            productMapper.deleteByPrimaryKey(id);
        }
    }

    /**************************** productCategorie ********************************/
    public List<CategoriesVo> getCategories() throws Exception {
        List<ProductCategory> productCategories = productCategoryMapper.selectAll();

        List<CategoriesVo> list = new ArrayList<>();
        list.add(new CategoriesVo("0",ConstantsUtil.ProductCategory.ALL_ID,"所有分类","0",null));
        for (ProductCategory parent : productCategories) {
            String parentPid = parent.getPid();
            if("0".equals(parentPid)){
                String parentId = parent.getId();
                String parentName = parent.getShopCategoryName();
                String parentSort = parent.getSort();
                List<CategoriesVo> childVoList = new ArrayList<>();
                for (ProductCategory child : productCategories) {
                    String childPid = child.getPid();
                    String childId = child.getId();
                    String childName = child.getShopCategoryName();
                    String childSort = parent.getSort();
                    if(childPid.equals(parentId)){
                        CategoriesVo childVo = new CategoriesVo(childPid,childId,childName,childSort,null);
                        childVoList.add(childVo);
                    }
                }
                CategoriesVo vo = new CategoriesVo(parentPid,parentId,parentName,parentSort,childVoList);
                list.add(vo);
            }
        }
        return list;
    }

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

    /**
     * 查询上架的所有商品
     * @param productListInput
     * @return
     */
    public SysResult getProductsByCategoryId(ProductListInput productListInput) {
        productListInput.setPageStart(productListInput.getPageNo()*productListInput.getPageSize());

        Set<String> ids = new HashSet<>();
        String categoryId = productListInput.getCategoryId();
        if(ConstantsUtil.ProductCategory.ALL_ID.equals(categoryId)){
            ids.add(categoryId);
        }else {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setPid(categoryId);
            List<ProductCategory> productCategories = productCategoryMapper.select(productCategory);
            if(productCategories.isEmpty()){
                ids.add(categoryId);
            }else {
                for (ProductCategory category : productCategories) {
                    ids.add(category.getId());
                }
            }
        }
        productListInput.setSkuIds(ids);

        int count = 0;
        List<ProductVo> list = null;
        if(ConstantsUtil.UserType.SENDER.equals(productListInput.getType())){
            count = productJdMapper.getProductsCountByCategoryId(productListInput);
            list = productJdMapper.getProductsByCategoryId(productListInput);
        }else if(ConstantsUtil.UserType.READYER.equals(productListInput.getType())){
            count = productJdMapper.getProductsCountByPhone(productListInput);
            list = productJdMapper.getProductsByPhone(productListInput);
        }else {
            list = new ArrayList<>();
        }
        return SysResult.ok(list,count);
    }

    /****************************** ProductStatus **************************************/
    public EasyUIResult queryProductStatusPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());

        ProductStatus product = new ProductStatus();
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

    public void updateProductStatusOfStorePriceVendibility(ProductStatus productStatus) throws Exception{
        log.info("ProductItemService updateProduct productStatus={}", productStatus);
        productStatusMapper.updateStoreAndStatus(productStatus);
    }

    public SysResult updateStorePriceVendibility(ProductListInput productListInput) throws Exception {
        Integer store = null,price = null;
        Byte vendibility = null;
        try{
            if(StringUtils.isNotBlank(productListInput.getSkuStore())&&StringUtils.isNotBlank(productListInput.getSkuPrice())){
                store = Integer.parseInt(productListInput.getSkuStore());
                price = Integer.parseInt(productListInput.getSkuPrice());
            }
            if(StringUtils.isNotBlank(productListInput.getVendibility())){
                vendibility = Byte.parseByte(productListInput.getVendibility());
            }
        }catch (Exception e){
            return SysResult.build(201,"数据格式不正确");
        }
        Long skuId = Long.parseLong(productListInput.getSkuId());
        String phone = productListInput.getPhone();
        String stationNo = productListInput.getStationNo();
        String type = productListInput.getType();
        if(ConstantsUtil.UserType.READYER.equals(type)){
            Product product = new Product();
            product.setPhone(phone);
            product.setBelongStationNo(stationNo);
            product.setSkuId(skuId);
            product.setIsSell(vendibility);
            product.setPrice(price);
            product.setStoreNum(store);
            updateProductOfStorePriceVendibility(product);
            /*ProductStatus productStatus = new ProductStatus();
            productStatus.setStationNo(stationNo);
            productStatus.setSkuId(Long.parseLong(skuId));
            updateProductStatusOfStorePrice(productStatus);*/
            return SysResult.build(200);
        }else {
            return SysResult.build(201,"未开放修改功能");
        }
    }

}
