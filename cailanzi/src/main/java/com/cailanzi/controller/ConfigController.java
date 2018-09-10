package com.cailanzi.controller;

import com.cailanzi.pojo.JdResult;
import com.cailanzi.service.ConfigService;
import com.cailanzi.utils.JdHttpCilentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by v-hel27 on 2018/9/10.
 */
@RestController
@Slf4j
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "saveToken",method = RequestMethod.POST)
    public JdResult saveToken(HttpServletRequest request){
        return configService.saveToken(request);
    }

    @RequestMapping(value = "getNewToken",method = RequestMethod.POST)
    public String getNewToken(){
        return JdHttpCilentUtil.JD_TAKEN;
    }

}
