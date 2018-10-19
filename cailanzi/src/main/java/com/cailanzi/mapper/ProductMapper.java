package com.cailanzi.mapper;

import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * Created by v-hel27 on 2018/8/8.
 */
@Mapper
public interface ProductMapper extends MyMapper<Product>{

    List<Product> selectDynamic(ProductListInput productListInput);

    void updateProductStatusEqualOrderShop(OrderListInput orderListInput);

    void updateProductOfStorePriceVendibility(Product product);

    Integer getRateBySkuId(Long skuId);

}
