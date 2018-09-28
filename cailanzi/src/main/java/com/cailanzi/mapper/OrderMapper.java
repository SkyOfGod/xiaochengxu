package com.cailanzi.mapper;

import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.OrderJdVo;
import com.cailanzi.pojo.OrderUnion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Mapper
public interface OrderMapper {

    List<OrderUnion> getOrderList(OrderListInput orderListInput);

    List<OrderUnion> getOrderShopJdList(OrderListInput orderListInput);
}
