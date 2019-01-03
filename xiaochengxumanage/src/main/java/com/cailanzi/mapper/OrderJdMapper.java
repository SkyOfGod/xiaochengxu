package com.cailanzi.mapper;

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

    List<OrderUnion> getOrderList(OrderListInput orderListInput);

    void updateOrderStatusByOrderId(OrderListInput orderListInput);

}
