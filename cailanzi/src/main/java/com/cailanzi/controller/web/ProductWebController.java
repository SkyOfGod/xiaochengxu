package com.cailanzi.controller.web;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.pojo.CategoriesVo;
import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.service.ProductItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/16.
 */
@Slf4j
@RestController
@RequestMapping("product/web")
public class ProductWebController {

    @Autowired
    private ProductItemService productItemService;

    @RequestMapping("getCategories")
    public List<CategoriesVo> getCategories() throws Exception {
        log.info("ProductWebController getCategories start");
        List<CategoriesVo> list = productItemService.getCategories();
        log.info("ProductWebController getCategories return {}", list);
        return list;
    }

    @RequestMapping(value = "getProductsByCategoryId",method = RequestMethod.POST)
    public SysResult getProductsByCategoryId(ProductListInput productListInput) throws Exception {
        log.info("ProductWebController getProductsByCategoryId productListInput={}",productListInput);
        SysResult sysResult = productItemService.getProductsByCategoryId(productListInput);
        log.info("ProductWebController getProductsByCategoryId return {}", sysResult);
        return sysResult;
    }

    @RequestMapping("updateStorePriceVendibility")
    public  SysResult updateStorePriceVendibility(ProductListInput productListInput) throws Exception {
        log.info("ProductWebController updateStorePriceVendibility productListInput={}",productListInput);
        try {
            SysResult sysResult = productItemService.updateStorePriceVendibility(productListInput);
            log.info("ProductWebController updateStorePriceVendibility return {}", sysResult);
            return sysResult;
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
    }


}
