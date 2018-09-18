package com.cailanzi.mapper;

import com.cailanzi.pojo.RuleListInput;
import com.cailanzi.pojo.entities.Rule;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Mapper
public interface RuleMapper extends MyMapper<Rule>{

    List<Rule> selectDynamic(RuleListInput ruleListInput);

}
