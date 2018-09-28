package com.cailanzi.rabbitMQ.rabbitListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Async.OrderAsync;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.rabbitMQ.messageNotify.pojo.MqOrder;
import com.cailanzi.mapper.OrderJdMapper;
import com.cailanzi.mapper.OrderShopMapper;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
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
    private OrderAsync orderAsync;
    @Autowired
    private OrderShopMapper orderShopMapper;
    @Autowired
    private OrderJdMapper orderJdMapper;

    @RabbitListener(queues = "order.create.one")
    public void orderCreateOne(MqOrder mqOrder){//京东状态码：32000
        log.info("OrderServiceListener orderCreateOne MqOrder mqOrder={}", mqOrder);
        if(orderAsync.isExitOrder(mqOrder.getBillId())){
            orderAsync.insertOrderShop(mqOrder.getBillId(),ConstantsUtil.Status.READY);
            return;
        }
        asynOrderJdByMqOrder(mqOrder);
    }

    @RabbitListener(queues = "order.create.two")
    public void orderCreateTwo(MqOrder mqOrder){//京东状态码：32000
        log.info("OrderServiceListener orderCreateTwo MqOrder mqOrder={}", mqOrder);
        if(orderAsync.isExitOrder(mqOrder.getBillId())){
            orderAsync.insertOrderShop(mqOrder.getBillId(),ConstantsUtil.Status.READY);
            return;
        }
        asynOrderJdByMqOrder(mqOrder);
    }

    @RabbitListener(queues = "order.delivery")
    public void deliveryToOrder(MqOrder mqOrder){//京东状态码：33040
        log.info("OrderServiceListener deliveryToOrder MqOrder mqOrder={}", mqOrder);
        if(!orderAsync.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener deliveryToOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(),ConstantsUtil.Status.DELIVERY_TO);
        }
    }

    @RabbitListener(queues = "order.finish")
    public void finishOrder(MqOrder mqOrder){//京东状态码：33060
        log.info("OrderServiceListener finishOrder MqOrder mqOrder={}", mqOrder);
        if(!orderAsync.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener finishOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(),ConstantsUtil.Status.FINISH);
        }
    }

    @RabbitListener(queues = "order.quit")
    public void quitOrder(MqOrder mqOrder){//京东状态码：20020
        log.info("OrderServiceListener quitOrder MqOrder mqOrder={}", mqOrder);
        if(!orderAsync.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener quitOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(),ConstantsUtil.Status.QUIT);
        }
    }

    public String getOrderListResultData(OrderListInput orderListInput) throws Exception {
        log.info("OrderServiceListener getOrderListResultData OrderInput orderInput={}", orderListInput);
        String url = "https://openo2o.jd.com/djapi/order/es/query";
        String jdParamJson = JdHttpCilentUtil.getJdParamJson(orderListInput);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        if(!"0".equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data.getString("result");
    }

    private void asynOrderJdByMqOrder(MqOrder mqOrder) {
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId(mqOrder.getBillId());
        orderListInput.setOrderStatus(mqOrder.getStatusId());
        try {
            String result = getOrderListResultData(orderListInput);
            JSONObject resultJson = JSON.parseObject(result);
            JSONArray jsonArray = resultJson.getJSONArray("resultList");
            if(jsonArray!=null&&!jsonArray.isEmpty()){
                orderAsync.insertOrderJd(jsonArray);
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
        orderJdMapper.updateOrderStatusByOrderId(orderId,status);
    }
}
