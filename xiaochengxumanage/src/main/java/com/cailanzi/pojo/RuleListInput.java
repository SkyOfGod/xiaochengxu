package com.cailanzi.pojo;

import lombok.Data;

/**
 * Created by v-hel27 on 2018/9/18.
 */
@Data
public class RuleListInput {

    //分页大小
    private Integer pageSize;

    private Integer pageNo;

    private Integer pageStart;

    private String name;
}
