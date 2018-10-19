package com.cailanzi.controller;

import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.rabbitmq.MqOrder;
import com.cailanzi.service.OrderService;
import com.cailanzi.websocket.AddOrderSocket;
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

    @RequestMapping("orderList")
    public EasyUIResult getOrderList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getOrderList OrderListInput orderListInput={}", orderListInput);
        EasyUIResult data = orderService.getOrderList(orderListInput);
        log.info("OrderController getOrderList return {}", data);
        return data;
    }

    @RequestMapping("orderProductList")
    public EasyUIResult getOrderProductList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getOrderProductList OrderListInput orderListInput={}", orderListInput);
        EasyUIResult data = orderService.getOrderProductList(orderListInput);
        log.info("OrderController getOrderProductList return {}", data);
        return data;
    }

    @RequestMapping("orderShopList")
    public EasyUIResult getOrderShopList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getOrderShopList OrderListInput orderListInput={}", orderListInput);
        EasyUIResult data = orderService.getOrderShopList(orderListInput);
        log.info("OrderController getShopOrderList return {}", data);
        return data;
    }

    @RequestMapping("deleteOrder")
    public SysResult deleteOrder(String orderId) throws Exception {
        log.info("OrderController deleteOrder String orderId={}", orderId);
        SysResult data = orderService.deleteOrder(orderId);
        return data;
    }

    //测试
    @Autowired
    private AddOrderSocket addOrderSocket;

    @RequestMapping("testSendMsg")
    public void testSendMsg(MqOrder mqOrder) throws Exception {
        addOrderSocket.orderCreateMsg(mqOrder);
    }

}
