package com.cailanzi.rabbitMQ.messageNotify.controller;

import com.alibaba.fastjson.JSONObject;
import com.cailanzi.rabbitMQ.messageNotify.pojo.JdOrderImport;
import com.cailanzi.rabbitMQ.messageNotify.pojo.MqOrder;
import com.cailanzi.rabbitMQ.messageNotify.service.OrderNotifyService;
import com.cailanzi.pojo.JdResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Slf4j
@RestController
@RequestMapping("/mq/notify/djsw")
public class OrderNotifyController {

    @Autowired
    private OrderNotifyService orderNotifyService;

    /**
     * 新订单消息接口
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "newOrder",method = RequestMethod.POST)
//    @RequestMapping(value = "newOrder",method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencodedd")
    public JdResult notifyNewOrder(HttpServletRequest request) throws Exception {
        JdOrderImport jdOrderImport = getJdOrderImport(request);
        log.info("OrderNotifyController notifyNewOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.notifyNewOrder(jdOrderImport);
    }

    private JdOrderImport getJdOrderImport(HttpServletRequest request) {
        JdOrderImport jdOrderImport = new JdOrderImport();

        String jd_param_json = request.getParameter("jd_param_json");
        if(jd_param_json != null){
            MqOrder mqOrder = JSONObject.toJavaObject(JSONObject.parseObject(jd_param_json),MqOrder.class);
            jdOrderImport.setJd_param_json(mqOrder);
        }

        jdOrderImport.setApp_key(request.getParameter("app_key"));
        jdOrderImport.setFormat(request.getParameter("format"));
        jdOrderImport.setSign(request.getParameter("sign"));
        jdOrderImport.setTimestamp(request.getParameter("timestamp"));
        jdOrderImport.setToken(request.getParameter("token"));
        jdOrderImport.setV(request.getParameter("v"));
        return jdOrderImport;
    }

    /**
     * 订单取消
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "userCancelOrder",method = RequestMethod.POST)
    public JdResult notifyUserCancelOrder(HttpServletRequest request) throws Exception {
        JdOrderImport jdOrderImport = getJdOrderImport(request);
        log.info("OrderNotifyController notifyUserCancelOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.notifyUserCancelOrder(jdOrderImport);
    }

    /**
     * 订单开始配送
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "deliveryOrder",method = RequestMethod.POST)
    public JdResult deliveryOrder(HttpServletRequest request) throws Exception {
        JdOrderImport jdOrderImport = getJdOrderImport(request);
        log.info("OrderNotifyController deliveryOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.deliveryOrder(jdOrderImport);
    }

    /**
     * 订单妥投
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "finishOrder",method = RequestMethod.POST)
    public JdResult finishOrder(HttpServletRequest request) throws Exception {
        JdOrderImport jdOrderImport = getJdOrderImport(request);
        log.info("OrderNotifyController finishOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.finishOrder(jdOrderImport);
    }


}
