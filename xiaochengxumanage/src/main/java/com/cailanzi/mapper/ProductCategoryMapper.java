package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.ProductCategory;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/12.
 */
@Mapper
public interface ProductCategoryMapper extends MyMapper<ProductCategory> {

    void batchInsertList(List<ProductCategory> list);

}
