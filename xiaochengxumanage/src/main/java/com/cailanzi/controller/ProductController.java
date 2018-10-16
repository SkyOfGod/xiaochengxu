package com.cailanzi.controller;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.ProductStatus;
import com.cailanzi.service.ProductItemService;
import com.cailanzi.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/8.
 */
@Slf4j
@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductItemService productItemService;

    @RequestMapping("jd/productPage")
    public EasyUIResult queryJdProductPage(ProductListInput productListInput) throws Exception {
        log.info("ProductController queryJdProductPage ProductListInput productListInput={}", productListInput);
        EasyUIResult sysResult = productItemService.queryJdProductPage(productListInput);
        log.info("ProductController queryJdProductPage return {}", sysResult);
        return sysResult;
    }

    @RequestMapping("productPage")
    public EasyUIResult queryProductPage(ProductListInput productListInput) throws Exception {
        log.info("ProductController queryProductPage ProductListInput productListInput={}", productListInput);
        EasyUIResult sysResult = productItemService.queryProductPage(productListInput);
        log.info("ProductController queryProductPage return {}", sysResult);
        return sysResult;
    }

    @RequestMapping("jd/asynProduct")
    public SysResult asynProduct() throws Exception {
        productService.asynProduct();
        return SysResult.ok(200);
    }

    @RequestMapping(value = "jd/comgridList",method = RequestMethod.POST)
    public List<ProductJd> comgridJdList(String q,String belongStationNo) throws Exception {
        return productItemService.comgridJdList(q,belongStationNo);
    }

    @RequestMapping(value = "addProduct",method = RequestMethod.POST)
    public SysResult addProduct(Product product) throws Exception {
        log.info("ProductController addProduct product = {}",product);
        try {
            productItemService.addProduct(product);
        }catch (ServiceException e){
            return SysResult.build(400,e.getMessage());
        }
        return SysResult.build(200);
    }

   /* @RequestMapping(value = "updateProduct",method = RequestMethod.POST)
    public SysResult updateProduct(Product product) throws Exception {
        log.info("ProductController updateProduct product={}",product);
        productItemService.updateProduct(product);
        return SysResult.build(200);
    }*/

    @RequestMapping(value = "updateProductOfStorePriceVendibility",method = RequestMethod.POST)
    public SysResult updateProductOfStorePriceVendibility(Product product) throws Exception {
        log.info("ProductController updateProductOfStorePriceVendibility product={}",product);
        try {
            productItemService.updateProductOfStorePriceVendibility(product);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.build(200);
    }

    @RequestMapping(value = "deleteProduct",method = RequestMethod.POST)
    public SysResult deleteProduct(String ids) throws Exception {
        log.info("ProductController deleteProduct ids={}",ids);
        productItemService.deleteProduct(ids);
        return SysResult.build(200);
    }

    @RequestMapping("jd/asynProductStatus")
    public SysResult asynProductStatus() {
        try {
            productService.asynProductStatus();
        } catch (Exception e) {
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.ok(200);
    }

    @RequestMapping("productStatusPage")
    public EasyUIResult queryProductStatusPage(ProductListInput productListInput) throws Exception {
        log.info("ProductController queryProductStatusPage ProductListInput productListInput={}", productListInput);
        EasyUIResult sysResult = productItemService.queryProductStatusPage(productListInput);
        log.info("ProductController queryProductStatusPage return {}", sysResult);
        return sysResult;
    }

    @RequestMapping("jd/asynCategories")
    public SysResult asynCategories() throws Exception {
        log.info("ProductController asynCategories start");
        productService.asynCategories();
        return SysResult.ok(200);
    }

    @RequestMapping("categoriesPage")
    public EasyUIResult categoriesPage(ProductListInput productListInput) throws Exception {
        log.info("ProductController categoriesPage start");
        EasyUIResult easyUIResult = productItemService.categoriesPage(productListInput);
        log.info("ProductController categoriesPage return {}", easyUIResult);
        return easyUIResult;
    }

    /**
     * 后台管理界面：修改京东商品（product_status）库存、价格、可售状态
     * @param productStatus
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "updateProductStatusOfStorePriceVendibility",method = RequestMethod.POST)
    public SysResult updateProductStatusOfStorePriceVendibility(ProductStatus productStatus) throws Exception {
        log.info("ProductController updateProductStatusOfStorePriceVendibility productStatus={}",productStatus);
        try {
            productItemService.updateProductStatusOfStorePriceVendibility(productStatus);
            return SysResult.build(200);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
    }

    @RequestMapping(value = "updateProductImg",method = RequestMethod.POST)
    public SysResult updateProductImg(ProductJd productjd) throws Exception {
        log.info("ProductController updateProductImg productjd={}",productjd);
        try {
            productItemService.updateProductImg(productjd);
            return SysResult.build(200);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
    }


}
