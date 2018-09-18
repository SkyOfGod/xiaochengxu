package com.cailanzi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/16.
 */
@Data
public class ProductStatusJdImport {

    private String outStationNo;

    private List<SkuIdEntity> skuIds;

    private String userPin;

}
