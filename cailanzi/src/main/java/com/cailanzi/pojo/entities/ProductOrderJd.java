package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Data
@Table(name = "product_order_jd")
public class ProductOrderJd {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private Long skuId;
    private String skuName;
    private Integer skuJdPrice;
    private Integer skuCount;
    private Integer skuStorePrice;
    private Integer skuCostPrice;
    private Float skuWeight;
    private Byte skuStatus;
}
