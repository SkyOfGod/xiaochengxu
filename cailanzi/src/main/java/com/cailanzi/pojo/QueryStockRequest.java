package com.cailanzi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by v-hel27 on 2018/9/19.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryStockRequest {

    private String stationNo;
    private String skuId;
    private String doSale;

}
