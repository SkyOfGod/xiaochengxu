package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/21.
 */
@Data
@Table(name = "product_img_category")
public class ProductImgCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String category;

    private String categoryName;

    private String belongFile;

    private Date createTime;
}
