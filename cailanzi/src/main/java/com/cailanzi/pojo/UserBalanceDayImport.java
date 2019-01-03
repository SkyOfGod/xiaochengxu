package com.cailanzi.pojo;

import lombok.Data;

/**
 * Created by v-hel27 on 2018/10/19.
 */
@Data
public class UserBalanceDayImport {

    //当前页
    private Integer pageNo;
    //分页大小
    private Integer pageSize;

    private String username;

    private Integer price;

    private String remark;

}
