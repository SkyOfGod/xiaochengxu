package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.Config;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/10.
 */
@Mapper
public interface ConfigMapper extends MyMapper<Config> {

    String getValueByName(String name);

    //数据库（服务器）的时间要早8个小时
    void updateValueByName(@Param("name") String name,@Param("value") String value,@Param("createTime") Date createTime);

    String getOneFormId();

    void updateValidByFormId(String formId);
}
