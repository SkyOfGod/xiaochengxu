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


    //客户名
    private String buyerFullName;
    //客户名（模糊查询）
    private String buyerFullName_like;
    //手机号
    private String buyerMobile;
    //订单支付类型（4：在线支付）
    private String orderPayType;
    //买家账号
    private String buyerPin;
    //订单开始时间(开始) 2016-05-05 00:00:00
    private String orderStartTime_begin;
    //订单开始时间(结束)
    private String orderStartTime_end;
    //购买成交时间-支付(开始)
    private String orderPurchaseTime_begin;
    //购买成交时间-支付(结束)
    private String orderPurchaseTime_end;
    //妥投时间(开始)
    private String deliveryConfirmTime_begin;
    //妥投时间(结束)
    private String deliveryConfirmTime_end;
    //订单关闭时间(开始)
    private String orderCloseTime_begin;
    //订单关闭时间(结束)
    private String orderCloseTime_end;
    //订单取消时间(开始)
    private String orderCancelTime_begin;
    //订单取消时间(结束)
    private String orderCancelTime_end;
    //城市编码，可以调用获取所有城市信息列表接口获取
    private String buyerCity_list;
    //承运单号，通常情况下和订单号一致
    private String deliveryBillNo;
    //业务类型（1:京东到家商超,2:京东到家美食,3:京东到家精品有约,4:京东到家开放仓,5:哥伦布店内订单,6:货柜项目订单,7:智能货柜项目订单,8:轻松购订单,9:自助收银订单,10:超级会员码），当多个业务类型时，是以逗号分隔的数值串
    private String businessType_list;
    //订单类型 10000:从门店出的订单
    private String orderType;
    //是否下发到商家中心（0 未下发；1已下发）
    private String order_sku_type;

}
