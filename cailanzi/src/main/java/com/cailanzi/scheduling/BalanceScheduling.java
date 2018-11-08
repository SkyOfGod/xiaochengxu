package com.cailanzi.scheduling;

import com.cailanzi.mapper.UserBalanceDayMapper;
import com.cailanzi.pojo.entities.UserBalanceDay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Slf4j
@Component
public class BalanceScheduling {

    @Autowired
    private UserBalanceDayMapper userBalanceDayMapper;

    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 27 19 * * ?")
    public void schedulTask(){
        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.minusDays(1);
        LocalDateTime localDateTime = LocalDateTime.now();
        log.info("BalanceScheduling schedulTask start localTime = {}",localDateTime);
        userBalanceDayMapper.statisticsUserBalanceEveryday(localDate,yesterday,localDateTime);
    }

}
