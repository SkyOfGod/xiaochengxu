package com.cailanzi;

import com.alibaba.fastjson.JSONObject;
import com.cailanzi.RabbitMQ.MessageNotify.pojo.MqOrder;
import com.cailanzi.RabbitMQ.RabbitListener.OrderServiceListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQtest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 1、点播
     */
    @Test
    public void send() {
        //message需要自己构造一个；定义消息体内容和消息头
        //rabbitTemplate.send(exchage,routeKey,message);

        //object默认当做消息体，只需要传入要发送的对象，自动序列化发送
        //rabbitTemplate.convertAndSend(exchage,routeKey,object);
        /*Map<String,Object> map = new HashMap<>();
        map.put("msg","这是第www个消息");
        map.put("data", Arrays.asList("helloworld",123,true));*/
        String data = "{'billId':'821461295000141','statusId':'32000','timestamp':'2015-10-16 13:23:30'}";
        MqOrder mqOrder = JSONObject.toJavaObject(JSONObject.parseObject(data), MqOrder.class);
        //默认是SimpleMessageConverter（java的序列化格式，在rabbitMQ管理后台看数据是乱码）
        rabbitTemplate.convertAndSend("exchange.direct","order.delivery.to", mqOrder);
//        rabbitTemplate.convertAndSend("exchange.direct","order.finish", mqOrder);
//        rabbitTemplate.convertAndSend("exchange.direct","order.quit.to", mqOrder);

    }

    @Test
    public void receive(){
        Object o = rabbitTemplate.receiveAndConvert("order.quit.to");
        System.out.println(o.getClass());
        System.out.println(o);
    }

    @Autowired
    private OrderServiceListener orderServiceListener;

    @Test
    public void testlistener() throws Exception {
        MqOrder mqOrder = new MqOrder();
        mqOrder.setBillId("821461295000141");
        mqOrder.setStatusId("32000");
        orderServiceListener.addOrder(mqOrder);
    }

}
