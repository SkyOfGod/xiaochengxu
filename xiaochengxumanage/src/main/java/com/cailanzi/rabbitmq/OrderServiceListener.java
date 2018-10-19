package com.cailanzi.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.OrderJdMapper;
import com.cailanzi.mapper.OrderShopMapper;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.service.OrderService;
import com.cailanzi.utils.ConstantsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Service
@Slf4j
public class OrderServiceListener {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderShopMapper orderShopMapper;
    @Autowired
    private OrderJdMapper orderJdMapper;

    @RabbitListener(queues = "order.create.one")
    public void orderCreateOne(MqOrder mqOrder){//京东状态码：32000
        log.info("OrderServiceListener orderCreateOne MqOrder mqOrder={}", mqOrder);
        if(orderService.isExitOrder(mqOrder.getBillId())){
            orderService.insertOrderShop(mqOrder.getBillId(), ConstantsUtil.Status.READY);
            return;
        }
        asynOrderJdByMqOrder(mqOrder);
    }

    @RabbitListener(queues = "order.delivery")
    public void deliveryToOrder(MqOrder mqOrder){//京东状态码：33040
        log.info("OrderServiceListener deliveryToOrder MqOrder mqOrder={}", mqOrder);
        if(!orderService.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener deliveryToOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(), ConstantsUtil.Status.DELIVERY_TO);
        }
    }

    @RabbitListener(queues = "order.finish")
    public void finishOrder(MqOrder mqOrder){//京东状态码：33060
        log.info("OrderServiceListener finishOrder MqOrder mqOrder={}", mqOrder);
        if(!orderService.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener finishOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(), ConstantsUtil.Status.FINISH);
        }
    }

    @RabbitListener(queues = "order.quit")
    public void quitOrder(MqOrder mqOrder){//京东状态码：20020
        log.info("OrderServiceListener quitOrder MqOrder mqOrder={}", mqOrder);
        if(!orderService.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener quitOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(), ConstantsUtil.Status.QUIT);
        }
    }

    private void asynOrderJdByMqOrder(MqOrder mqOrder) {
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId(mqOrder.getBillId());
        orderListInput.setOrderStatus(mqOrder.getStatusId());
        try {
            String result = orderService.getOrderListResultData(orderListInput);
            JSONObject resultJson = JSON.parseObject(result);
            JSONArray jsonArray = resultJson.getJSONArray("resultList");//订单列表
            if(jsonArray!=null&&!jsonArray.isEmpty()){
                orderService.insertOrderJd(jsonArray);
            }
        } catch (Exception e) {
            log.info("OrderServiceListener addOrder:",e);
        }
    }

    public void updateOrderStatus(String orderId,String status) {
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId(orderId);
        orderListInput.setOrderStatus(status);
        orderListInput.setUpdateTime(new Date());
        orderShopMapper.updateOrderStatus(orderListInput);
        orderJdMapper.updateOrderStatusByOrderId(orderListInput);
    }

}
