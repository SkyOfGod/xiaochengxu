package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Data
@Table(name = "rule")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer rate;
    private Byte isValid;
    private Date createTime;
    private Date updateTime;

}
