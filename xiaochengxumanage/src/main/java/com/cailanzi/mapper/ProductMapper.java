package com.cailanzi.mapper;

import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/8.
 */
@Mapper
public interface ProductMapper extends MyMapper<Product> {

    List<Product> selectDynamic(ProductListInput productListInput);

    void updateProductOfStorePriceVendibility(Product product);

    Integer getRateBySkuId(Long skuId);
}
