package com.cailanzi.controller;

import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.UserBalanceDayImport;
import com.cailanzi.service.UserChargeLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Slf4j
@RestController
@RequestMapping("userChargeLog")
public class UserChargeLogController {

    @Autowired
    private UserChargeLogService UserChargeLogService;

    @RequestMapping("listPage")
    public EasyUIResult listPage(UserBalanceDayImport userBalanceDayImport){
        log.info("UserChargeLogController listPage userBalanceDayImport={}", userBalanceDayImport);
        EasyUIResult sysResult = UserChargeLogService.listPage(userBalanceDayImport);
        log.info("UserChargeLogController listPage return {}", sysResult);
        return sysResult;
    }

}
