package com.cailanzi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by v-hel27 on 2018/10/4.
 */
@Service
@Slf4j
public class AddOrderSocket {

    @RabbitListener(queues = "order.create.msg")
    public void orderCreateMsg(MqOrder mqOrder) throws IOException {
        log.info("AddOrderSocket orderCreateMsg MqOrder mqOrder={}", mqOrder);
        WebSocketServer.sendInfo(mqOrder.toString());
    }

}
