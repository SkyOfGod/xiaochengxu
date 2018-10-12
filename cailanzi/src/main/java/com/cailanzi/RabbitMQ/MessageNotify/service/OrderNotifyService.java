package com.cailanzi.rabbitMQ.messageNotify.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.cailanzi.rabbitMQ.messageNotify.pojo.JdOrderImport;
import com.cailanzi.rabbitMQ.messageNotify.pojo.MqOrder;
import com.cailanzi.pojo.JdResult;
import com.cailanzi.service.ConfigService;
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
    private ConfigService configService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final static String EXCHANGE_DIRECT = "exchange.direct";

    private final static String ADD_ROUTING_KEY = "order.create";

    private final static String QUIT_ROUTING_KEY = "order.quit";

    private final static String DELIVERY_TO_ROUTING_KEY = "order.delivery";

    private final static String FINISH_ROUTING_KEY = "order.finish";

    public JdResult notifyNewOrder(JdOrderImport jdOrderImport) throws Exception {
        return notifyOrder(jdOrderImport,ADD_ROUTING_KEY);
    }

    public JdResult notifyUserCancelOrder(JdOrderImport jdOrderImport) throws Exception {
        return notifyOrder(jdOrderImport,QUIT_ROUTING_KEY);
    }

    public JdResult deliveryOrder(JdOrderImport jdOrderImport) throws Exception {
        return notifyOrder(jdOrderImport,DELIVERY_TO_ROUTING_KEY);
    }

    public JdResult finishOrder(JdOrderImport jdOrderImport) throws Exception {
        return notifyOrder(jdOrderImport,FINISH_ROUTING_KEY);
    }

    public JdResult notifyOrder(JdOrderImport jdOrderImport,String routingKey) throws Exception {
        JdResult jdResult = checkNotifyParam(jdOrderImport);
        if("0".equals(jdResult.getCode())){
            log.info("OrderNotifyService notifyUserCancelOrder convertAndSend jd_param_json={}", jdOrderImport.getJd_param_json());
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,routingKey,jdOrderImport.getJd_param_json());
        }
        return jdResult;
    }

    private JdResult checkNotifyParam(JdOrderImport jdOrderImport) throws Exception {
        String code = "0";
        String msg = "success";
        Object data = jdOrderImport;

        String token = jdOrderImport.getToken();
        String app_key = jdOrderImport.getApp_key();
        String timestamp = jdOrderImport.getTimestamp();
        String format = jdOrderImport.getFormat();
        String v = jdOrderImport.getV();
        MqOrder mqOrder = jdOrderImport.getJd_param_json();

        String sign = jdOrderImport.getSign();
        if(StringUtils.isBlank(token)||StringUtils.isBlank(app_key)||StringUtils.isBlank(timestamp)||StringUtils.isBlank(format)
                ||StringUtils.isBlank(v)||mqOrder==null||StringUtils.isBlank(sign)){
            code =  "10005";
            msg = "必填项参数未填";
        }else {
            if(!JdHttpCilentUtil.TAKEN_JD.equals(token)){
                code = "10013";
                msg = "无效Token令牌";
            } else {
                if(!JdHttpCilentUtil.APP_KEY.equals(app_key)){
                    code = "10015";
                    msg = "API参数异常:app_key无效";
                }else {
                    Map<String,String> param = new HashMap<>();
                    param.put("v",v);
                    param.put("format",format);
                    param.put("app_key",app_key);
                    param.put("token",token);

                    String jd_param_json = "{\"billId\":\""+mqOrder.getBillId()+"\",\"statusId\":\""+mqOrder.getStatusId()+"\",\"timestamp\":\""+mqOrder.getTimestamp()+"\"}";
                    param.put("jd_param_json",jd_param_json);
                    param.put("timestamp",timestamp);
                    String getSign = JdHelper.getSign(param,JdHttpCilentUtil.APP_SECRET);
                    if(!getSign.equals(sign)){
                        code = "10014";
                        msg = "无效Sign签名";
                    }else {
                        data = "";
                    }
                }
            }
        }
        return JdResult.build(code,msg,data);
    }

    /*public static void main(String[] args) throws Exception {
        MqOrder mqOrder = new MqOrder();
        mqOrder.setBillId("10003129");
        mqOrder.setTimestamp("2015-10-16 13:23:30");
        mqOrder.setStatusId("32000");
        System.out.println(mqOrder.toString());

        String jd_param_json = "{\"billId\":\"10003129\",\"statusId\":\"32000\",\"timestamp\":\"2015-10-16 13:23:30\"}";
        String str = "{\"billId\":\""+mqOrder.getBillId()+"\",\"statusId\":\""+mqOrder.getStatusId()+"\",\"timestamp\":\""+mqOrder.getTimestamp()+"\"}";
        Map<String,String> param = new HashMap<>();
        param.put("v","1.0");
        param.put("format","json");
        param.put("app_key","6bd9123fd3224c4299e06c9a9651a5cf");
        param.put("token","e298bab2-9c58-4838-8105-b317498be342");
        param.put("jd_param_json",str);
        param.put("timestamp","2018-09-10 10:39:12");
        String sign = "9BB21D1495A13C15F51414F312F7FEAB";
        String getSign = JdHelper.getSign(param,JdHttpCilentUtil.APP_SECRET);
        System.out.println(getSign);
    }*/

}
