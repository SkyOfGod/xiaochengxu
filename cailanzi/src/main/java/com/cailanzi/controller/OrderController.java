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

    //待备货数据
    @RequestMapping("web/orderList")
    private SysResult getWebOrderList(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrderList OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrderList(orderListInput);
        log.info("OrderController getWebOrderList return {}", data);
        return data;
    }

    //待收货数据
    @RequestMapping("web/order2List")
    private SysResult getWebOrder2List(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrder2List OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrder2List(orderListInput);
        log.info("OrderController getWebOrder2List return {}", data);
        return data;
    }

    //待配送数据
    @RequestMapping("web/order3List")
    private SysResult getWebOrder3List(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrder3List OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrder3List(orderListInput);
        log.info("OrderController getWebOrder3List return {}", data);
        return data;
    }

    //已完成数据
    @RequestMapping("web/order4List")
    private SysResult getWebOrder4List(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrder4List OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrder4List(orderListInput);
        log.info("OrderController getWebOrder4List return {}", data);
        return data;
    }

    //已退订单数据
    @RequestMapping("web/order5List")
    private SysResult getWebOrder5List(OrderListInput orderListInput) throws Exception {
        log.info("OrderController getWebOrder5List OrderListInput orderListInput={}", orderListInput);
        SysResult data = orderService.getWebOrder5List(orderListInput);
        log.info("OrderController getWebOrder5List return {}", data);
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

    @RequestMapping("deleteOrder")
    private SysResult deleteOrder(String orderId) throws Exception {
        log.info("OrderController deleteOrder String orderId={}", orderId);
        SysResult data = orderService.deleteOrder(orderId);
        log.info("OrderController deleteOrder return {}", data);
        return data;
    }

    /**
     * 将订单状态改为待收货
     * @param orderListInput
     * @return
     * @throws Exception
     */
    @RequestMapping("web/updateOrderStatusToDelivery")
    private SysResult updateOrderStatusToDelivery(OrderListInput orderListInput) throws Exception {
        log.info("OrderController updateOrderStatusToDelivery OrderListInput orderListInput={}", orderListInput);
        return orderService.updateOrderStatusToDelivery(orderListInput);
    }

    /**
     * 商品转缺货
     * @param orderListInput
     * @return
     * @throws Exception
     */
    @RequestMapping("web/updateProductToStockout")
    private SysResult updateProductToStockout(OrderListInput orderListInput) throws Exception {
        log.info("OrderController updateOrderShopStatusToFinish OrderListInput orderListInput={}", orderListInput);
        return orderService.updateProductToStockout(orderListInput);
    }



}
