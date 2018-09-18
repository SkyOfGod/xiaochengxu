package com.cailanzi.pojo;

import lombok.Data;

import java.util.Set;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@Data
public class ProductListInput {

    private String phone;
    //到家门店编码
    private String stationNo;
    //到家商品编码
    private String skuId;
    //商家商品上下架状态(1:上架;2:下架;4:删除;)
    private String fixedStatus;
    //可售状态(0:可售 1:不可售)
    private String vendibility;

    private String categoryId;

    private String categoryPid;
    //角色类型
    private String type;

    //商品名称(支持模糊查询)
    private String skuName;
    //商品UPC编码
    private String upcCode;
    //当前页
    private Integer pageNo;
    //分页大小
    private Integer pageSize;

    private Integer pageStart;

    private Set<String> skuIds;

    private String skuPrice;

    private String skuStore;
}
