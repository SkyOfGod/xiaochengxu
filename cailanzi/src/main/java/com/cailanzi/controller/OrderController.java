package com.cailanzi.controller;

import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    //待发货数据
    @RequestMapping("web/orderList")
    private SysResult getWebOrderList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrderList OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrderList(orderListInput);
        log.info("OrderController getWebOrderList return {}", data);
        return data;
    }

    //待配送数据
    @RequestMapping("web/order2List")
    private SysResult getWebOrder2List(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrder2List OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrder2List(orderListInput);
        log.info("OrderController getWebOrder2List return {}", data);
        return data;
    }

    @RequestMapping("orderList")
    private EasyUIResult getOrderList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getOrderList OrderListInput orderListInput={}", orderListInput);
        EasyUIResult data = orderService.getOrderList(orderListInput);
        log.info("OrderController getOrderList return {}", data);
        return data;
    }

    @RequestMapping("orderShopList")
    private EasyUIResult getOrderShopList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getOrderShopList OrderListInput orderListInput={}", orderListInput);
        EasyUIResult data = orderService.getOrderShopList(orderListInput);
        log.info("OrderController getShopOrderList return {}", data);
        return data;
    }

    @RequestMapping("web/updateOrderShopStatus")
    private SysResult updateOrderShopStatus(OrderListInput orderListInput) throws Exception {
        log.info("OrderController updateOrderShopStatus OrderListInput orderListInput={}", orderListInput);
        return orderService.updateOrderShopStatus(orderListInput);
    }



}
