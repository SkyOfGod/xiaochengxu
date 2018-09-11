package com.cailanzi.MQTT.producer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.SendResult;

import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.cailanzi.MQTT.MqConfig;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.MqOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Properties;

/**
 * Created by v-hel27 on 2018/9/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendTest {

    @Test
    public void simpleMQProducer(){
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.ProducerId, MqConfig.PRODUCER_ID);
        producerProperties.setProperty(PropertyKeyConst.AccessKey, MqConfig.ACCESS_KEY);
        producerProperties.setProperty(PropertyKeyConst.SecretKey, MqConfig.SECRET_KEY);
        producerProperties.setProperty(PropertyKeyConst.ONSAddr, MqConfig.ONSADDR);
        Producer producer = ONSFactory.createProducer(producerProperties);
        producer.start();
        System.out.println("Producer Started");

        String data = "{'billId':'821461295000141','statusId':'32000','timestamp':'2015-10-16 13:23:30'}";
        Message message = new Message(MqConfig.TOPIC, MqConfig.TAG, data.getBytes());
        try {
            SendResult sendResult = producer.send(message);
            assert sendResult != null;
            System.out.println(new Date() + " Send mq message success! Topic is:" + MqConfig.TOPIC + " msgId is: " + sendResult.getMessageId());
        } catch (ONSClientException e) {
            System.out.println("发送失败");
            //出现异常意味着发送失败，为了避免消息丢失，建议缓存该消息然后进行重试。
        }
    }

    @Test
    public void simpleOrderProducer(){
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.ProducerId, MqConfig.ORDER_PRODUCER_ID);
        producerProperties.setProperty(PropertyKeyConst.AccessKey, MqConfig.ACCESS_KEY);
        producerProperties.setProperty(PropertyKeyConst.SecretKey, MqConfig.SECRET_KEY);
        producerProperties.setProperty(PropertyKeyConst.ONSAddr, MqConfig.ONSADDR);
        OrderProducer producer = ONSFactory.createOrderProducer(producerProperties);
        producer.start();
        System.out.println("Producer Started");

        for (int i = 0; i < 10; i++) {
            Message msg = new Message(MqConfig.ORDER_TOPIC, MqConfig.TAG, "mq send order message test".getBytes());
            // 设置代表消息的业务关键属性，请尽可能全局唯一。
            String orderId = "biz_1" + i;
            msg.setKey(orderId);
            // 分区顺序消息中区分不同分区的关键字段，sharding key于普通消息的key是完全不同的概念。
            // 全局顺序消息，该字段可以设置为任意非空字符串。
            String shardingKey = String.valueOf(orderId);
            try {
                SendResult sendResult = producer.send(msg, shardingKey);
                assert sendResult != null;
                System.out.println(new Date() + " Send mq message success! Topic is:" + MqConfig.ORDER_TOPIC + " msgId is: " + sendResult.getMessageId());
            } catch (ONSClientException e) {
                System.out.println("发送失败");
                //出现异常意味着发送失败，为了避免消息丢失，建议缓存该消息然后进行重试。
            }
        }
    }

    @Test
    public void MQTimerProducer(){
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.ProducerId, MqConfig.PRODUCER_ID);
        producerProperties.setProperty(PropertyKeyConst.AccessKey, MqConfig.ACCESS_KEY);
        producerProperties.setProperty(PropertyKeyConst.SecretKey, MqConfig.SECRET_KEY);
        producerProperties.setProperty(PropertyKeyConst.ONSAddr, MqConfig.ONSADDR);
        Producer producer = ONSFactory.createProducer(producerProperties);
        producer.start();
        System.out.println("Producer Started");

        for (int i = 0; i < 10; i++) {
            Message message = new Message(MqConfig.TOPIC, MqConfig.TAG, "mq send timer message test".getBytes());
            // 延时时间单位为毫秒（ms），指定一个时刻，在这个时刻之后才能被消费，这个例子表示 3秒 后才能被消费
            long delayTime = 3000;
            message.setStartDeliverTime(System.currentTimeMillis() + delayTime);
            SendResult sendResult = producer.send(message);
            if (sendResult != null) {
                System.out.println(new Date() + " Send mq timer message success! Topic is:" + MqConfig.TOPIC + "msgId is: " + sendResult.getMessageId());
            }
        }
    }

}
