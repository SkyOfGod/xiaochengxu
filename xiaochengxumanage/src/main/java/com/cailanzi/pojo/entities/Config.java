package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/10.
 */
@Data
@Table(name = "config")
public class Config {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String value;
    private String remark;
    private Date createTime;

}
