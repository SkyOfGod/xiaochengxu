package com.cailanzi.pojo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/12.
 */
@Table(name = "product_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {

    @Id
    private String id;
    private String pid;
    private String shopCategoryName;
    private String shopCategoryLevel;
    private String sort;
    private Date createTime;

}
