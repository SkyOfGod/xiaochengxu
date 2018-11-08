package com.cailanzi.mapper;

import com.cailanzi.pojo.UserBalanceDayImport;
import com.cailanzi.pojo.entities.UserBalanceDay;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Mapper
public interface UserBalanceDayMapper extends MyMapper<UserBalanceDay>{


    List<String> selectMultile(@Param("usernames") String[] usernames);

    void updateBalance(@Param("price") Integer price,@Param("usernames") String[] usernames,@Param("date") Date date);

    void statisticsUserBalanceEveryday(@Param("localDate") LocalDate localDate,@Param("yesterday") LocalDate yesterday,@Param("localDateTime")LocalDateTime localDateTime);

    List<UserBalanceDay> selectDynamic(UserBalanceDayImport userBalanceDayImport);
}
