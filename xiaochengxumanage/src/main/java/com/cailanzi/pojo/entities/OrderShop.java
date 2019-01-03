package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Data
@Table(name = "order_shop")
public class OrderShop {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String belongStationNo;
    private String orderId;
    private String orderStatus;
    private Long skuId;
    private String skuName;
    private Integer skuCount;
    private Integer skuPrice;
    private Byte skuStatus;
    private Date createTime;
    private Date updateTime;

}
