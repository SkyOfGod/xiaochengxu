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

    /**
     *
     * @param q
     * @param belongStationNo
     * @param username 暂时取消使用，包装门店下关联唯一性
     * @return
     */
    List<ProductJd> comgridJdList(@Param("q") String q,@Param("belongStationNo") String belongStationNo,@Param("username") String username);

    List<ProductVo> getProductsByCategoryId(ProductListInput productListInput);

    int getProductsCountByCategoryId(ProductListInput productListInput);

    List<ProductVo> getProductsByPhone(ProductListInput productListInput);

    int getProductsCountByPhone(ProductListInput productListInput);

    List<ProductJd> selectByStationNoLeftJoinProductStatus(String stationNo);

    void backupImgUrl();

    void callImgUrl();

    void truncateProductJdBak();

    List<ProductJd> getProductJdBySkuIds(@Param("skuIds")String[] skuIds,@Param("belongStationNo") String belongStationNo);

    List<ProductJd> productJdComgridExcludeInRuleProduct(@Param("q") String q);
}
