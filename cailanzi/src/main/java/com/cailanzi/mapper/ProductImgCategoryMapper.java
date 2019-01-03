package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.ProductImgCategory;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/21.
 */
@Mapper
public interface ProductImgCategoryMapper extends MyMapper<ProductImgCategory>{

    List<ProductImgCategory> imgCategoryComgrid(@Param("q") String q);

}
