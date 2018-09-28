package com.cailanzi.mapper;

import com.cailanzi.pojo.ProductListInput;
import com.cailanzi.pojo.ProductVo;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/12.
 */
@Mapper
public interface ProductJdMapper extends MyMapper<ProductJd> {

    List<ProductJd> selectDynamic(ProductListInput productListInput);

    void truncateProductJd();

    List<ProductJd> comgridJdList(@Param("q") String q,@Param("belongStationNo") String belongStationNo);

    List<ProductVo> getProductsByCategoryId(ProductListInput productListInput);

    int getProductsCountByCategoryId(ProductListInput productListInput);

    List<ProductVo> getProductsByPhone(ProductListInput productListInput);

    int getProductsCountByPhone(ProductListInput productListInput);

    List<ProductJd> selectByStationNoLeftJoinProductStatus(String stationNo);

    List<ProductJd> productJdComgrid(@Param("q") String q);

    void backupImgUrl();

    void callImgUrl();

    void truncateProductJdBak();
}
