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

    List<OrderUnion> getOrderShopList(OrderListInput orderListInput);

    /**
     *
     * @param orderId
     * @param skuIds
     * @return
     */
    List<OrderShop> getProductStatus(@Param("orderId") String orderId,@Param("skuIds") Set<String> skuIds);

    /**
     * order_shop中不在status中的订单id
     * @param username
     * @param orderIds
     * @param status
     * @return
     */
    Set<String> getOutReadyStatusOrderIdsOfOrderShop(@Param("username") String username,@Param("orderIds") Set<String> orderIds,@Param("status") String status);

    /**
     * 通过订单id获取插入order_shop的原始数据
     * @param orderId
     * @return
     */
    List<OrderShop> getOrderShopListInitData(String orderId);

    void updateOrderStatus(OrderListInput orderListInput);

    void updateProductStatus(OrderListInput orderListInput);

    void updateProductStatusToDelivery(OrderListInput orderListInput);
}
