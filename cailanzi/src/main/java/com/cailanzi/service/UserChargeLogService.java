package com.cailanzi.service;

import com.cailanzi.mapper.UserChargeLogMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.UserBalanceDayImport;
import com.cailanzi.pojo.entities.UserBalanceDay;
import com.cailanzi.pojo.entities.UserChargeLog;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Slf4j
@Service
public class UserChargeLogService {

    @Autowired
    private UserChargeLogMapper userChargeLogMapper;

    public EasyUIResult listPage(UserBalanceDayImport userBalanceDayImport) {
        PageHelper.startPage(userBalanceDayImport.getPageNo(),userBalanceDayImport.getPageSize());
        UserChargeLog userChargeLog = new UserChargeLog();
        if(StringUtils.isNotBlank(userBalanceDayImport.getUsername())){
            userChargeLog.setUsername(userBalanceDayImport.getUsername());
        }
        List<UserChargeLog> list = userChargeLogMapper.select(userChargeLog);
        PageInfo<UserChargeLog> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
