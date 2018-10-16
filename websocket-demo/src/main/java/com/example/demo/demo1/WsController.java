package com.example.demo.demo1;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Created by v-hel27 on 2018/9/29.
 */
@Controller
public class WsController {

    @MessageMapping("/welconme")
    @SendTo("/topic/getResponse")
    public WiselyResponse say(WiselyMessage message) throws Exception {
        Thread.sleep(3000);
        return new WiselyResponse("Welcome" + message.getName() + "!");
    }


}
