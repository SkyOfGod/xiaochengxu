package com.cailanzi.controller;

import com.cailanzi.exception.ServiceException;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.RuleProductListInput;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.Rule;
import com.cailanzi.pojo.entities.RuleProduct;
import com.cailanzi.service.RuleProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Slf4j
@RestController
@RequestMapping("ruleProduct")
public class RuleProductController {

    @Autowired
    private RuleProductService ruleProductService;

    @RequestMapping("ruleProductPage")
    private EasyUIResult ruleProductPage(RuleProductListInput ruleProductListInput) throws Exception {
        log.info("RuleProductController ruleProductPage RuleListInput ruleProductListInput={}", ruleProductListInput);
        EasyUIResult data = ruleProductService.ruleProductPage(ruleProductListInput);
        log.info("RuleProductController ruleProductPage return {}", data);
        return data;
    }

    @RequestMapping(value = "ruleCompgird",method = RequestMethod.POST)
    private List<Rule> ruleCompgird(String q) throws Exception {
        return ruleProductService.ruleCompgird(q);
    }

    @RequestMapping(value = "productJdComgrid",method = RequestMethod.POST)
    private List<ProductJd> productJdComgrid(String q) throws Exception {
        return ruleProductService.productJdComgrid(q);
    }

    @RequestMapping(value = "addRuleProduct",method = RequestMethod.POST)
    public SysResult addRuleProduct(RuleProduct ruleProduct) throws Exception {
        log.info("RuleProductController addRuleProduct ruleProduct = {}",ruleProduct);
        try {
            ruleProductService.addRuleProduct(ruleProduct);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.build(200);
    }

    @RequestMapping(value = "deleteRuleProduct",method = RequestMethod.POST)
    public SysResult deleteRuleProduct(String ids) throws Exception {
        log.info("RuleController deleteRule ids = {}",ids);
        ruleProductService.deleteRuleProduct(ids);
        return SysResult.build(200);
    }
}
