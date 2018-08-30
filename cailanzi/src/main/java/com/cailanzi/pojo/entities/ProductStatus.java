package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/8/16.
 */
@Data
@Table(name = "product_status")
public class ProductStatus {

    private String stationNo;

    private Long skuId;

    private Integer usableQty;

    private Integer lockQty;

    private Integer orderQty;

    private Byte vendibility;

    private Date createTime = new Date();

}
