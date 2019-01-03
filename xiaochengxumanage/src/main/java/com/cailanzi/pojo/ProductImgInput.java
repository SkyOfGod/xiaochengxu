package com.cailanzi.pojo;

import lombok.Data;

/**
 * Created by v-hel27 on 2018/9/24.
 */
@Data
public class ProductImgInput {

    //当前页
    private Integer pageNo;
    //分页大小
    private Integer pageSize;

    private String name;

}
