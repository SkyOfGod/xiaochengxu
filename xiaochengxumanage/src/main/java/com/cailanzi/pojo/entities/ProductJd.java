package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/8/12.
 */
@Data
@Table(name = "product_jd")
public class ProductJd {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Long skuId;
    private String outSkuId;
    private String skuName;
    private Integer skuPrice;
    private String upcCode;
    private Long categoryId;
    private String shopCategories;
    private String sellCities;
    private Integer stockNum;
    private Byte fixedStatus;
    private Integer orgCode;
    private Date fixedUpTime;
    private Date fixedDownTime;
    private Date syncTime;
    private String imgUrl;

}
