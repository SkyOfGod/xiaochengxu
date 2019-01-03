package com.cailanzi.mapper;

import com.cailanzi.pojo.RuleProductListInput;
import com.cailanzi.pojo.entities.RuleProduct;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Mapper
public interface RuleProductMapper extends MyMapper<RuleProduct> {

    void deleteByRuleIds(@Param("ruleIds") Set<String> ruleIds);

    List<RuleProduct> selectDynamic(RuleProductListInput ruleProductListInput);

}
