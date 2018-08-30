package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.ProductStatus;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by v-hel27 on 2018/8/16.
 */
@Mapper
public interface ProductStatusMapper extends MyMapper<ProductStatus>{

    void truncateProductStatus();

    void updateStoreAndStatus(ProductStatus productStatus);
}
