package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.TokenJd;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by v-hel27 on 2018/9/10.
 */
@Mapper
public interface ConfigMapper extends MyMapper<TokenJd> {

    String getNewToken();

}
