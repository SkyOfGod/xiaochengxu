package com.cailanzi.pojo;

import lombok.Data;

import java.util.List;

/**
 * Created by v-hel27 on 2018/8/9.
 */
@Data
public class CategoriesVo {

    private String pid;

    private String id;

    private String name;

    private String sort;

    private boolean open;

    private List<CategoriesVo> child;

    public CategoriesVo(String pid, String id, String name, String sort, List<CategoriesVo> child) {
        this.pid = pid;
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.child = child;
    }

    public CategoriesVo(){}
}
