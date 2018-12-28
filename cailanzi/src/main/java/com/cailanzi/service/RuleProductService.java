package com.cailanzi.service;

import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.ProductJdMapper;
import com.cailanzi.mapper.RuleMapper;
import com.cailanzi.mapper.RuleProductMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.RuleListInput;
import com.cailanzi.pojo.RuleProductListInput;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.Rule;
import com.cailanzi.pojo.entities.RuleProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Slf4j
@Service
public class RuleProductService {

    @Autowired
    private RuleProductMapper ruleProductMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private ProductJdMapper productJdMapper;

    public EasyUIResult ruleProductPage(RuleProductListInput ruleProductListInput) {
        PageHelper.startPage(ruleProductListInput.getPageNo(),ruleProductListInput.getPageSize());

        List<RuleProduct> list = ruleProductMapper.selectDynamic(ruleProductListInput);
        log.info("RuleProductService ruleProductPage list={}", list);
        PageInfo<RuleProduct> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public List<Rule> ruleCompgird(String q) {
        log.info("RuleProductService ruleCompgird q={}", q);
        RuleListInput ruleListInput = new RuleListInput();
        ruleListInput.setName(q);
        List<Rule> list = ruleMapper.selectDynamic(ruleListInput);
        log.info("RuleProductService ruleCompgird return list={}", list);
        return list;
    }

    public List<ProductJd> productJdComgrid(String q) {
        log.info("RuleProductService productJdComgrid q={}", q);
        List<ProductJd> list = productJdMapper.productJdComgridExcludeInRuleProduct(q);
        log.info("RuleProductService productJdComgrid return list={}", list);
        return list;
    }

    public void addRuleProduct(RuleProduct ruleProduct) throws ServiceException{
        RuleProduct newRuleProduct = new RuleProduct();
        newRuleProduct.setSkuId(ruleProduct.getSkuId());
        List<RuleProduct> list = ruleProductMapper.select(newRuleProduct);
        if(!list.isEmpty()){
            throw new ServiceException("商品["+ruleProduct.getSkuName()+"]已经分组到["+list.get(0).getRuleName()+"]");
        }
        ruleProduct.setCreateTime(new Date());
        log.info("RuleProductService addRuleProduct ruleProduct={}", ruleProduct);
        ruleProductMapper.insert(ruleProduct);
    }

    public void deleteRuleProduct(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            ruleProductMapper.deleteByPrimaryKey(id);
        }
    }
}
