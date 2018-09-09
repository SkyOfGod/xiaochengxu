package com.cailanzi.mapper;

import com.cailanzi.pojo.OrderJdVo;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.OrderUnion;
import com.cailanzi.pojo.entities.OrderJd;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Mapper
public interface OrderJdMapper extends MyMapper<OrderJd> {

    void updateOrderStatusByOrderId(@Param("orderId") String orderId,@Param("status") String status);

    /**
     * 获取order_jd不在status状态下的订单
     * @param orderIds
     * @param status
     * @return
     */
    Set<String> getDeliveryOrderIdsOfOrderJd(@Param("orderIds") Set<String> orderIds,@Param("status") String status);

    /**
     * 修改商品状态
     * @param orderListInput
     */
    void updateProductStatus(OrderListInput orderListInput);

}
