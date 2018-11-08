package com.cailanzi.controller;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.UserBalanceDayImport;
import com.cailanzi.service.UserBalanceDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Slf4j
@RestController
@RequestMapping("userBalanceDay")
public class UserBalanceDayController {

    @Autowired
    private UserBalanceDayService userBalanceDayService;

    @RequestMapping("listPage")
    public EasyUIResult listPage(UserBalanceDayImport userBalanceDayImport){
        log.info("UserBalanceDayController listPage userBalanceDayImport={}", userBalanceDayImport);
        EasyUIResult sysResult = userBalanceDayService.listPage(userBalanceDayImport);
        log.info("UserBalanceDayController listPage return {}", sysResult);
        return sysResult;
    }

    @RequestMapping(value = "addUserBalance",method = RequestMethod.POST)
    public SysResult addUserBalance(UserBalanceDayImport userBalanceDayImport) throws Exception {
        log.info("UserBalanceDayController addUserBalance userBalanceDayImport = {}",userBalanceDayImport);
        try {
            userBalanceDayService.addUserBalance(userBalanceDayImport);
        }catch (ServiceException e){
            return SysResult.build(400,e.getMessage());
        }
        return SysResult.build(200);
    }


}
