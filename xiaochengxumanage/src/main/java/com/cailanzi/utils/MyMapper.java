package com.cailanzi.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by v-hel27 on 2018/8/12.
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
