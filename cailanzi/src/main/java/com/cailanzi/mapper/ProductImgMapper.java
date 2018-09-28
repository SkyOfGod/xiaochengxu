package com.cailanzi.mapper;

import com.cailanzi.pojo.ProductImgInput;
import com.cailanzi.pojo.ProductImgUnion;
import com.cailanzi.pojo.entities.ProductImg;
import com.cailanzi.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by v-hel27 on 2018/9/21.
 */
@Mapper
public interface ProductImgMapper extends MyMapper<ProductImg>{

    List<ProductImgUnion> productImgComgrid(@Param("q") String q);

    List<ProductImg> selectDynamic(ProductImgInput productImgInput);

}
