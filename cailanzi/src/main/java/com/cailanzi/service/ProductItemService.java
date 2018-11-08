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
import org.springframework.transaction.annotation.Transactional;

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

    public List<ProductJd> comgridJdList(String q,String belongStationNo,String username) {
        log.info("ProductItemService comgridJdList q={}", q);
        List<ProductJd> list = productJdMapper.comgridJdList(q,belongStationNo,username);
        log.info("ProductItemService comgridJdList return list={}", list);
        return list;
    }

    public EasyUIResult addProductPage(ProductListInput productListInput) {
        PageHelper.startPage(productListInput.getPageNo(),productListInput.getPageSize());
        List<ProductJd> list = new ArrayList<>();
        if(StringUtils.isNotBlank(productListInput.getStationNo())&&StringUtils.isNotBlank(productListInput.getPhone())){
            list = productJdMapper.comgridJdList(null,productListInput.getStationNo(),productListInput.getPhone());
        }
        PageInfo<ProductJd> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public void addProduct(ProductListInput productListInput) throws ServiceException{
        Date date = new Date();
        String[] skuIds = productListInput.getSkuId().split(",");
        List<ProductJd> list = productJdMapper.getProductJdBySkuIds(skuIds,productListInput.getStationNo());
        List<Product> inList = new ArrayList<>();
        for (ProductJd productJd :list) {
            Product product = new Product();
            product.setPhone(productListInput.getPhone());
            product.setBelongStationNo(productListInput.getStationNo());
            product.setBelongStationName(productListInput.getStationName());

            product.setSkuId(productJd.getSkuId());
            product.setName(productJd.getSkuName());
            product.setPrice(productJd.getSkuPrice());
            product.setStoreNum(productJd.getStockNum());
            product.setShopCategories(productJd.getShopCategories());
//            product.setDescription(productListInput.getDescription());
            product.setIsSell((byte)0);
            product.setCreateTime(date);
            product.setUpdateTime(date);
            inList.add(product);
        }
        log.info("ProductItemService addProduct inList={}", inList);
        productMapper.insertList(inList);
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

    @Transactional
    public SysResult updateStorePriceVendibility(ProductListInput productListInput) throws Exception {
        Integer store = null,price = null;
        Byte vendibility = null;
        Product product = new Product();
        try{
            if(StringUtils.isNotBlank(productListInput.getSkuStore())){
                store = Integer.parseInt(productListInput.getSkuStore());
                product.setStoreNum(store);
            }
            if(StringUtils.isNotBlank(productListInput.getSkuPrice())){
                price = Integer.parseInt(productListInput.getSkuPrice());
                product.setPrice(price);
            }
            if(StringUtils.isNotBlank(productListInput.getVendibility())){
                vendibility = Byte.parseByte(productListInput.getVendibility());
                product.setIsSell(vendibility);
            }
        }catch (Exception e){
            return SysResult.build(201,"数据格式不正确");
        }
        Long skuId = Long.parseLong(productListInput.getSkuId());
        String phone = productListInput.getPhone();
        String stationNo = productListInput.getStationNo();
        String type = productListInput.getType();
        if(ConstantsUtil.UserType.READYER.equals(type)){
            product.setPhone(phone);
            product.setBelongStationNo(stationNo);
            product.setSkuId(skuId);
            updateProductOfStorePriceVendibility(product);
            return SysResult.build(200);
        } else {
            return SysResult.build(201,"未开放修改功能");
        }
    }

    public void updateProductImg(ProductJd productjd) {
        productJdMapper.updateByPrimaryKeySelective(productjd);
    }


}
