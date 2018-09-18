package com.cailanzi.pojo;

import lombok.Data;

/**
 * Created by v-hel27 on 2018/8/17.
 */
@Data
public class ProductOrderJdVo {

    private String skuId;
    private String skuName;
    private String skuCount;
    private String skuStorePrice;
    private String status;
    private int cost;//skuCount*skuStorePrice

}
