package com.cailanzi.RabbitMQ.MessageNotify.controller;

import com.alibaba.fastjson.JSONObject;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.JdOrderImport;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.MqOrder;
import com.cailanzi.RabbitMQ.MessageNotify.service.OrderNotifyService;
import com.cailanzi.pojo.JdResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Slf4j
@Controller
@RequestMapping("/mq/notify/djsw")
public class OrderNotifyController {

    @Autowired
    private OrderNotifyService orderNotifyService;

    /**
     * 新订单消息接口
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "newOrder",method = RequestMethod.POST)
//    @RequestMapping(value = "newOrder",method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencodedd")
    public JdResult notifyNewOrder(HttpServletRequest request) throws Exception {
        JdOrderImport jdOrderImport = getJdOrderImport(request);
        log.info("OrderNotifyController notifyNewOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.notifyNewOrder(jdOrderImport);
    }

    private JdOrderImport getJdOrderImport(HttpServletRequest request) {
        String jd_param_json = request.getParameter("jd_param_json");
        MqOrder mqOrder = JSONObject.toJavaObject(JSONObject.parseObject(jd_param_json),MqOrder.class);

        JdOrderImport jdOrderImport = new JdOrderImport();
        jdOrderImport.setJd_param_json(mqOrder);
        jdOrderImport.setApp_key(request.getParameter("app_key"));
        jdOrderImport.setFormat(request.getParameter("format"));
        jdOrderImport.setSign(request.getParameter("sign"));
        jdOrderImport.setTimestamp(request.getParameter("timestamp"));
        jdOrderImport.setToken(request.getParameter("token"));
        jdOrderImport.setV(request.getParameter("v"));
        return jdOrderImport;
    }

    @ResponseBody
    @RequestMapping(value = "userCancelOrder",method = RequestMethod.POST)
    public JdResult notifyUserCancelOrder(HttpServletRequest request) throws Exception {
        JdOrderImport jdOrderImport = getJdOrderImport(request);
        log.info("OrderNotifyController notifyUserCancelOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.notifyUserCancelOrder(jdOrderImport);
    }


}
