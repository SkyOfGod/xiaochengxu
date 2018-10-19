package com.cailanzi.mapper;

import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.entities.ProductStatus;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/16.
 */
@Mapper
public interface ProductStatusMapper extends MyMapper<ProductStatus> {

    void updateProductStatusOfStorePriceVendibility(ProductStatus productStatus);

    List<ProductStatus> selectDynamic(ProductListInput productListInput);
}
