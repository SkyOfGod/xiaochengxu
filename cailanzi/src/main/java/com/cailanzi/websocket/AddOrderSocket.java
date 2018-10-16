package com.cailanzi.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.UserMapper;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.entities.User;
import com.cailanzi.rabbitMQ.messageNotify.pojo.MqOrder;
import com.cailanzi.rabbitMQ.rabbitListener.OrderServiceListener;
import com.cailanzi.utils.WxHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by v-hel27 on 2018/10/4.
 */
@Service
@Slf4j
public class AddOrderSocket {

    @Autowired
    private OrderServiceListener orderServiceListener;
    @Autowired
    private UserMapper userMapper;

    @RabbitListener(queues = "order.create.msg")
    public void orderCreateMsg(MqOrder mqOrder) throws Exception {
        log.info("AddOrderSocket orderCreateMsg MqOrder mqOrder={}", mqOrder);
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId(mqOrder.getBillId());
        orderListInput.setOrderStatus(mqOrder.getStatusId());

        String result = orderServiceListener.getOrderListResultData(orderListInput);
        JSONObject resultJson = JSON.parseObject(result);
        JSONArray jsonArray = resultJson.getJSONArray("resultList");
        log.info("AddOrderSocket getUserList resultList={}", jsonArray);
        JSONObject orderJsonObject = JSON.parseObject(jsonArray.get(0).toString());
        String orderNum = orderJsonObject.getString("orderNum");

        List<User> list = getUserList(mqOrder,orderJsonObject);
        Set<String> usernames = new HashSet<>();
        for (User user : list) {
            usernames.add(user.getUsername());
            String openId = user.getOpenId();
            if(StringUtils.isNotBlank(openId)){
                JSONObject keyword1 = new JSONObject();
                keyword1.put("value",mqOrder.getBillId()+" #"+orderNum);
                JSONObject keyword2 = new JSONObject();
                keyword2.put("value",mqOrder.getTimestamp());
                JSONObject data = new JSONObject();
                data.put("keyword1",keyword1);
                data.put("keyword2",keyword2);
//                data.put("emphasis_keyword","keyword1.DATA");
                WxHttpClientUtil.sendTemplateMsg(openId,data);
            }
        }
        WebSocketServer.sendInfo(mqOrder.toString(),usernames);
    }

    private List<User> getUserList(MqOrder mqOrder,JSONObject orderJsonObject) throws Exception {
        String stationNo = orderJsonObject.getString("produceStationNo");
        User user = new User();
        user.setBelongStationNo(stationNo);
        return userMapper.select(user);
    }


}
