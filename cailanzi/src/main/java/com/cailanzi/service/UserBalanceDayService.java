package com.cailanzi.service;

import com.cailanzi.mapper.UserBalanceDayMapper;
import com.cailanzi.mapper.UserChargeLogMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.UserBalanceDayImport;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.UserBalanceDay;
import com.cailanzi.pojo.entities.UserChargeLog;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Slf4j
@Service
public class UserBalanceDayService {

    @Autowired
    private UserBalanceDayMapper userBalanceDayMapper;
    @Autowired
    private UserChargeLogMapper userChargeLogMapper;

    public EasyUIResult listPage(UserBalanceDayImport userBalanceDayImport) {
        PageHelper.startPage(userBalanceDayImport.getPageNo(),userBalanceDayImport.getPageSize());
        List<UserBalanceDay> list = userBalanceDayMapper.selectDynamic(userBalanceDayImport);
        PageInfo<UserBalanceDay> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Transactional
    public void addUserBalance(UserBalanceDayImport userBalanceDayImport) {
        Date date = new Date();
        Integer price = userBalanceDayImport.getPrice()*100;
        String remark = userBalanceDayImport.getRemark();
        String[] usernames = userBalanceDayImport.getUsername().split(",");
        log.info("UserBalanceDayService addUserBalance update usernames={},price={}",usernames,price);
        userBalanceDayMapper.updateBalance(price,usernames,date);

        List<String> list = userBalanceDayMapper.selectMultile(usernames);
        List<UserBalanceDay> userBalanceDayList = new ArrayList<>();
        List<UserChargeLog> userChargeLogList= new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        for (String username : usernames) {
            if(!list.contains(username)){
                UserBalanceDay userBalanceDay = new UserBalanceDay();
                userBalanceDay.setUsername(username);
                userBalanceDay.setBalance(price);
                userBalanceDay.setCreateDate(localDate);
                userBalanceDay.setUpdateTime(date);
                userBalanceDayList.add(userBalanceDay);
            }
            UserChargeLog userChargeLog = new UserChargeLog();
            userChargeLog.setUsername(username);
            userChargeLog.setCharge(price);
            userChargeLog.setRemark(remark);
            userChargeLog.setCreateTime(date);
            userChargeLogList.add(userChargeLog);
        }
        if(!userBalanceDayList.isEmpty()){
            log.info("UserBalanceDayService addUserBalance insert userBalanceDayList={}",userBalanceDayList);
            userBalanceDayMapper.insertList(userBalanceDayList);
        }
        if(!userChargeLogList.isEmpty()){
            log.info("UserBalanceDayService addUserBalance insert userChargeLogList={}",userChargeLogList);
            userChargeLogMapper.insertList(userChargeLogList);
        }
    }
}
