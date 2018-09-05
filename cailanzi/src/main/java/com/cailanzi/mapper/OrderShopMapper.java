package com.cailanzi.mapper;

import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.OrderUnion;
import com.cailanzi.pojo.entities.OrderShop;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Mapper
public interface OrderShopMapper extends MyMapper<OrderShop> {

    List<OrderShop> getOrderShopPageList(OrderListInput orderListInput);

    void updateOrderShopStatus(OrderListInput orderListInput);

    List<OrderUnion> getOrderShopList(@Param("username") String username,@Param("stationNo") String stationNo,@Param("status") String status);

    List<OrderShop> getProductStatus(@Param("orderId") String orderId,@Param("skuIds") Set<String> skuIds);

    Set<String> getOutReadyStatusOrderIdsOfOrderShop(@Param("username") String username,@Param("orderIds") Set<String> orderIds,@Param("status") String status);
}
