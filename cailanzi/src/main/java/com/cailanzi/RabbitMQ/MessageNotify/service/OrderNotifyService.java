package com.cailanzi.RabbitMQ.MessageNotify.service;

import com.alibaba.fastjson.JSONObject;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.JdOrderImport;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.MqOrder;
import com.cailanzi.pojo.JdResult;
import com.cailanzi.utils.JdHelper;
import com.cailanzi.utils.JdHttpCilentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Service
@Slf4j
public class OrderNotifyService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final static String EXCHANGE_DIRECT = "exchange.direct";

    private final static String ADD_ROUTING_KEY = "order.add";

    private final static String QUIT_ROUTING_KEY = "order.quit";

    public JdResult notifyNewOrder(JdOrderImport jdOrderImport) throws Exception {
        return notifyOrder(jdOrderImport,ADD_ROUTING_KEY);
    }

    public JdResult notifyUserCancelOrder(JdOrderImport jdOrderImport) throws Exception {
        return notifyOrder(jdOrderImport,QUIT_ROUTING_KEY);
    }

    public JdResult notifyOrder(JdOrderImport jdOrderImport,String routingKey) throws Exception {
        Integer code = checkNotifyParam(jdOrderImport);
        if(code != 0){
            return JdResult.build(code);
        }
        log.info("OrderNotifyService notifyUserCancelOrder convertAndSend jd_param_json={}", jdOrderImport.getJd_param_json());
        MqOrder mqOrder = JSONObject.toJavaObject(JSONObject.parseObject(jdOrderImport.getJd_param_json()), MqOrder.class);
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,routingKey,mqOrder);
        return JdResult.ok(0);
    }

    private Integer checkNotifyParam(JdOrderImport jdOrderImport) throws Exception {
        String token = jdOrderImport.getToken();
        String app_key = jdOrderImport.getApp_key();
        String timestamp = jdOrderImport.getTimestamp();
        String format = jdOrderImport.getFormat();
        String v = jdOrderImport.getV();
        String jd_param_json = jdOrderImport.getJd_param_json();

        String sign = jdOrderImport.getSign();
        if(StringUtils.isBlank(token)||StringUtils.isBlank(app_key)||StringUtils.isBlank(timestamp)||StringUtils.isBlank(format)
                ||StringUtils.isBlank(v)||StringUtils.isBlank(jd_param_json)||StringUtils.isBlank(sign)){
            return 10005;
        }
        if(!JdHttpCilentUtil.JD_TAKEN.equals(token)){
            return 10013;
        }
        if(!JdHttpCilentUtil.APP_KEY.equals(app_key)){
            return 10015;
        }

        Map<String,String> param = new HashMap<>();
        param.put("v",v);
        param.put("format",format);
        param.put("app_key",app_key);
        param.put("token",token);
        param.put("jd_param_json",jd_param_json);
        param.put("timestamp",timestamp);
        String getSign = JdHelper.getSign(param,JdHttpCilentUtil.APP_SECRET);
        if(!getSign.equals(sign)){
            return 10014;
        }
        return 0;
    }


}
