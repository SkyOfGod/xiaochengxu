package com.cailanzi.mapper;

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

    void truncateProductJd();

    List<ProductJd> comgridJdList(@Param("q") String q,@Param("belongStationNo") String belongStationNo);

    List<ProductVo> getProductsByCategoryId(@Param("stationNo") String stationNo, @Param("categoryId") String categoryId,
                                            @Param("startIndex") int startIndex,@Param("pageSize") int pageSize);

    List<ProductVo> getProductsByPhone(@Param("phone") String phone,@Param("stationNo") String stationNo, @Param("categoryId") String categoryId,
                                       @Param("startIndex") int startIndex,@Param("pageSize") int pageSize);
}
