package com.cailanzi.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/8/17.
 */
@Data
public class OrderJdVo {

    private String orderId;
    private Integer orderNum;
    private String orderBuyerRemark;
    private Date createTime;
    private List<ProductOrderJdVo> product;

    private boolean open = false;//默认不展开

}
