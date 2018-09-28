package com.cailanzi.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Data
public class OrderUnion {

    private String orderId;
    private Integer orderNum;
    private String produceStationNo;
    private String produceStationName;
    private String buyerFullName;
    private String buyerFullAddress;
    private String buyerMobile;
    private String status;
    private String orderBuyerRemark;
    private Date createTime;
    private String orderStartTime;

    private Long skuId;
    private String skuName;
    private String skuCount;
    private String skuStorePrice;
    private Byte skuStatus;

    private Integer skuJdPrice;
    private Integer skuCostPrice;
    private Float skuWeight;
    private String imgUrl;

}
