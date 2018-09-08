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
@Table(name = "order_jd")
public class OrderJd {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private Integer orderNum;
    private String produceStationNo;
    private String produceStationName;
    private String buyerFullName;
    private String buyerFullAddress;
    private String buyerMobile;
    private String status;
    private Date createTime;
    private String orderBuyerRemark;

}
