package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/8/14.
 */
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String phone;

    private String belongStationNo;

    private String belongStationName;

    private String shopCategories;

    private Long skuId;

    private String name;

    private Integer price;

    private Integer storeNum;

    private Byte isSell;

    private String description;

    private String imgUrl;

    private Date createTime;

    private Date updateTime;
}
