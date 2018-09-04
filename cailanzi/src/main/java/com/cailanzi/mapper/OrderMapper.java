package com.cailanzi.mapper;

import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.OrderJdVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Mapper
public interface OrderMapper {

    List<OrderJdVo> getOrderList(OrderListInput orderListInput);

}
