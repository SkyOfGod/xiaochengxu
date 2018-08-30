package com.cailanzi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by v-hel27 on 2018/8/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusInput {

    private String stationNo;

    private Long skuId;

}
