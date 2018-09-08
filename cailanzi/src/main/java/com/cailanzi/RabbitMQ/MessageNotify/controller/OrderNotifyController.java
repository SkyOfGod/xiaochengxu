package com.cailanzi.RabbitMQ.MessageNotify.controller;

import com.cailanzi.RabbitMQ.MessageNotify.pojo.JdOrderImport;
import com.cailanzi.RabbitMQ.MessageNotify.service.OrderNotifyService;
import com.cailanzi.pojo.JdResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value = "newOrder")
    public JdResult notifyNewOrder(JdOrderImport jdOrderImport) throws Exception {
        log.info("OrderNotifyController notifyNewOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.notifyNewOrder(jdOrderImport);
    }

    @RequestMapping(value = "userCancelOrder")
    public JdResult notifyUserCancelOrder(JdOrderImport jdOrderImport) throws Exception {
        log.info("OrderNotifyController notifyUserCancelOrder JdOrderImport jdOrderImport={}", jdOrderImport);
        return orderNotifyService.notifyUserCancelOrder(jdOrderImport);
    }


}
