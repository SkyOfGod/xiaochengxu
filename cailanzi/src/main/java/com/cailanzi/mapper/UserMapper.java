package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.User;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/11.
 */
@Mapper
public interface UserMapper extends MyMapper<User>{

    List<User> comgridList(String q);

    List<User> selectByUsername(@Param("username") String username,@Param("type") Integer type);
}
