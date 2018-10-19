package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.ShopJd;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/13.
 */
@Mapper
public interface ShopMapper extends MyMapper<ShopJd> {

    void truncateShopJd();

    List<ShopJd> getCompgirdList(@Param("q") String q);
}
