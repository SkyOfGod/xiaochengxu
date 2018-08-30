package com.cailanzi.pojo;

import lombok.Data;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/17.
 */
@Data
public class OrderVo {

    private String orderId;

    private List<OrderProductVo> product;


}
