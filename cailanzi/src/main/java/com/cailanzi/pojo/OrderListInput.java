package com.cailanzi.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@Data
public class OrderListInput {

    private String username;

    private String belongStationNo;

    //每页条数,默认：20，超过100也只返回100条
    private int pageSize = 20;
    //当前页数,默认：1
    private int pageNo = 1;
    //订单号
    private String orderId;
    //订单状态
    private String orderStatus;
    //到家门店编码
    private String deliveryStationNo;
    //角色类型
    private String type;

    private Date updateTime;

    private String skuId;

    private String skuStatus;

}
