package com.cailanzi.service;

import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.RuleMapper;
import com.cailanzi.mapper.RuleProductMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.RuleListInput;
import com.cailanzi.pojo.entities.Rule;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Slf4j
@Service
public class RuleService {

    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private RuleProductMapper ruleProductMapper;

    public EasyUIResult rulePage(RuleListInput ruleListInput) {
        PageHelper.startPage(ruleListInput.getPageNo(),ruleListInput.getPageSize());

        List<Rule> list = ruleMapper.selectDynamic(ruleListInput);
        log.info("RuleService rulePage list={}", list);
        PageInfo<Rule> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public void addRule(Rule rule) throws ServiceException{
        validateRuleName(rule);
        rule.setCreateTime(new Date());
        rule.setUpdateTime(rule.getCreateTime());
        log.info("RuleService addRule rule={}", rule);
        ruleMapper.insert(rule);
    }

    private void validateRuleName(Rule rule) throws ServiceException{
        Rule newRule = new Rule();
        newRule.setName(rule.getName());
        List<Rule> list = ruleMapper.select(newRule);
        if(!list.isEmpty()){
            throw new ServiceException("已经存在名称为["+rule.getName()+"]的数据");
        }
    }

    public void updateRule(Rule rule) {
        Rule oldRule = ruleMapper.selectByPrimaryKey(rule.getId());
        if(!oldRule.getName().equals(rule.getName())){
            validateRuleName(rule);
        }
        rule.setUpdateTime(new Date());
        log.info("RuleService updateRule rule={}", rule);
        ruleMapper.updateByPrimaryKeySelective(rule);
    }

    public void deleteRule(String ids) {
        String[] arr = ids.split(",");
        Set<String> ruleIds = new HashSet<>();
        for (String id : arr) {
            ruleMapper.deleteByPrimaryKey(id);
            ruleIds.add(id);
        }
        ruleProductMapper.deleteByRuleIds(ruleIds);
    }

}
