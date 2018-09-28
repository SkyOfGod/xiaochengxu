package com.cailanzi.controller;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.ProductImg;
import com.cailanzi.pojo.entities.ProductImgCategory;
import com.cailanzi.service.ProductImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by v-hel27 on 2018/9/21.
 */
@Slf4j
@RestController
@RequestMapping("img")
public class ProductImgController {

    @Autowired
    private ProductImgService productImgService;

    @RequestMapping("productImgComgrid")
    public List<ProductImgUnion> productImgComgrid(String q){
        return productImgService.productImgComgrid(q);
    }

    @RequestMapping("imgPage")
    public EasyUIResult queryImgPage(ProductImgInput productImgInput) throws Exception {
        log.info("ProductImgController queryImgPage ProductImgInput productImgInput={}", productImgInput);
        EasyUIResult sysResult = productImgService.queryImgPage(productImgInput);
        log.info("ProductImgController queryImgPage return {}", sysResult);
        return sysResult;
    }

    @RequestMapping(value = "updateImgName",method = RequestMethod.POST)
    public SysResult updateImgName(ProductImg productImg) throws Exception {
        log.info("ProductImgController updateImgName productImg={}",productImg);
        productImgService.updateImgName(productImg);
        return SysResult.build(200);
    }

    @RequestMapping(value = "deleteImg",method = RequestMethod.POST)
    public SysResult deleteImg(String ids, HttpServletRequest request) throws Exception {
        log.info("ProductImgController deleteImg ids={}",ids);
        productImgService.deleteImg(ids,request.getServletContext().getRealPath("/"));
        return SysResult.build(200);
    }


    @RequestMapping(value = "imgCategoryComgrid",method = RequestMethod.POST)
    public List<ProductImgCategory> imgCategoryComgrid(String q) throws Exception {
        return productImgService.imgCategoryComgrid(q);
    }

    @RequestMapping("imgCategoryPage")
    public EasyUIResult queryImgCategoryPage(ProductImgInput productImgInput) throws Exception {
        log.info("ProductImgController queryImgCategoryPage ProductImgInput productImgInput={}", productImgInput);
        EasyUIResult sysResult = productImgService.queryImgCategoryPage(productImgInput);
        log.info("ProductImgController queryImgCategoryPage return {}", sysResult);
        return sysResult;
    }

    @RequestMapping(value = "addImgCategory",method = RequestMethod.POST)
    public SysResult addImgCategory(ProductImgCategory productImgCategory) throws Exception {
        log.info("ProductImgController addImgCategory productImgCategory = {}",productImgCategory);
        try {
            productImgService.addImgCategory(productImgCategory);
        }catch (ServiceException e){
            return SysResult.build(400,e.getMessage());
        }
        return SysResult.build(200);
    }



}
