package com.example.demo.demo2;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/9/27.
 */
@RestController
public class controller {

    @RequestMapping(value="/pushVideoListToWeb",method= RequestMethod.POST,consumes = "application/json")
    public Map<String,Object> pushVideoListToWeb(@RequestBody Map<String,Object> param) {
        Map<String,Object> result =new HashMap<String,Object>();
        try {
            WebSocketServer.sendInfo("有新客户呼入,sltAccountId:"+param.get("sltAccountId"));
            result.put("operationResult", true);
        } catch (IOException e) {
            result.put("operationResult", true);
        }
        return result;
    }


}
