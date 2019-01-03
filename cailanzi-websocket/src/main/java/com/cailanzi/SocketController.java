package com.cailanzi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by v-hel27 on 2018/10/12.
 */
@RestController
@Slf4j
public class SocketController {

    @RequestMapping("send")
    public String send() throws IOException {
        WebSocketServer.sendInfo("发消息来了");
        return "SUCCES";
    }
}
