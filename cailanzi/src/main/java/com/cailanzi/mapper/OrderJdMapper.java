package com.cailanzi.mapper;

import com.cailanzi.pojo.entities.OrderJd;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Mapper
public interface OrderJdMapper extends MyMapper<OrderJd> {

    void updateStatusByOrderId(@Param("orderId") String orderId,@Param("status") String status);

    Set<String> getDeliveryOrderIdsOfOrderJd(@Param("orderIds") Set<String> orderIds,@Param("status") String status);
}
