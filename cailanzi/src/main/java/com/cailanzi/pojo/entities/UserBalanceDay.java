package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Data
@Table(name = "user_balance_day")
public class UserBalanceDay {

    private String username;
    private Integer balance;
    private Integer yesterdayBill;
    private LocalDate createDate;
    private Date updateTime;

}
