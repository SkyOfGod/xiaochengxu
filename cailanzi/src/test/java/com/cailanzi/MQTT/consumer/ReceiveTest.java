package com.cailanzi.MQTT.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.cailanzi.MQTT.MqConfig;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.MqOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.aliyun.openservices.ons.api.Action;

import java.util.Properties;
import java.util.Date;

import com.aliyun.openservices.ons.api.Message;

/**
 * Created by v-hel27 on 2018/9/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReceiveTest {

    @Test
    public void simpleMQConsumer() {
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(PropertyKeyConst.ConsumerId, MqConfig.CONSUMER_ID);
        consumerProperties.setProperty(PropertyKeyConst.AccessKey, MqConfig.ACCESS_KEY);
        consumerProperties.setProperty(PropertyKeyConst.SecretKey, MqConfig.SECRET_KEY);
        consumerProperties.setProperty(PropertyKeyConst.ONSAddr, MqConfig.ONSADDR);
        Consumer consumer = ONSFactory.createConsumer(consumerProperties);
//        consumer.subscribe(MqConfig.TOPIC, MqConfig.TAG, new MessageListenerImpl());
        consumer.subscribe(MqConfig.TOPIC, MqConfig.TAG, new MessageListener(){
            @Override
            public Action consume(Message message, ConsumeContext consumeContext) {
                System.out.println(new Date() + " Receive message, Topic is:" +
                        message.getTopic() + ", MsgId is:" + message.getMsgID()+ ", Body is:" + message.getBody());
                MqOrder mqOrder = JSONObject.toJavaObject(JSONObject.parseObject(new String(message.getBody())), MqOrder.class);
                System.out.println(mqOrder);
                //如果想测试消息重投的功能,可以将Action.CommitMessage 替换成Action.ReconsumeLater
                return Action.CommitMessage;
            }
        });
        consumer.start();
        System.out.println("Consumer start success.");

        //等待固定时间防止进程退出
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void simpleOrderConsumer() {
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(PropertyKeyConst.ConsumerId, MqConfig.ORDER_CONSUMER_ID);
        consumerProperties.setProperty(PropertyKeyConst.AccessKey, MqConfig.ACCESS_KEY);
        consumerProperties.setProperty(PropertyKeyConst.SecretKey, MqConfig.SECRET_KEY);
        consumerProperties.setProperty(PropertyKeyConst.ONSAddr, MqConfig.ONSADDR);
        OrderConsumer consumer = ONSFactory.createOrderedConsumer(consumerProperties);
        consumer.subscribe(MqConfig.ORDER_TOPIC, MqConfig.TAG, new MessageOrderListener() {

            @Override
            public OrderAction consume(final Message message, final ConsumeOrderContext context) {
                System.out.println(message);
                return OrderAction.Success;
            }
        });
        consumer.start();
        System.out.println("Consumer start success.");

        //等待固定时间防止进程退出
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
