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

    void updateOrderStatus(OrderListInput orderListInput);

    /**
     * 通过订单id获取插入order_shop的原始数据
     * @param orderId
     * @return
     */
    List<OrderShop> getOrderShopListInitData(String orderId);

}
