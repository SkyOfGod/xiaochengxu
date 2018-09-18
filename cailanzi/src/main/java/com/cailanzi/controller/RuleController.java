package com.cailanzi.controller;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.RuleListInput;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.Rule;
import com.cailanzi.service.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Slf4j
@RestController
@RequestMapping("rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @RequestMapping("rulePage")
    private EasyUIResult rulePage(RuleListInput ruleListInput) throws Exception {
        log.info("RuleController rulePage RuleListInput ruleListInput={}", ruleListInput);
        EasyUIResult data = ruleService.rulePage(ruleListInput);
        log.info("RuleController rulePage return {}", data);
        return data;
    }

    @RequestMapping(value = "addRule",method = RequestMethod.POST)
    public SysResult addRule(Rule rule) throws Exception{
        log.info("RuleController addRule rule = {}",rule);
        try {
            ruleService.addRule(rule);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.build(200);
    }

    @RequestMapping(value = "updateRule",method = RequestMethod.POST)
    public SysResult updateRule(Rule rule) throws Exception{
        try {
            ruleService.updateRule(rule);
            log.info("RuleController updateRule rule = {}",rule);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.build(200);
    }

    @RequestMapping(value = "deleteRule",method = RequestMethod.POST)
    public SysResult deleteRule(String ids) throws Exception {
        log.info("RuleController deleteRule ids = {}",ids);
        ruleService.deleteRule(ids);
        return SysResult.build(200);
    }
}
