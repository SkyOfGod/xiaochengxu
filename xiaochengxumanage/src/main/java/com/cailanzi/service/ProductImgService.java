package com.cailanzi.service;

import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.ProductImgCategoryMapper;
import com.cailanzi.mapper.ProductImgMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.ProductImgInput;
import com.cailanzi.pojo.ProductImgUnion;
import com.cailanzi.pojo.entities.ProductImg;
import com.cailanzi.pojo.entities.ProductImgCategory;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/9/21.
 */
@Slf4j
@Service
public class ProductImgService {

    @Autowired
    private ProductImgMapper productImgMapper;
    @Autowired
    private ProductImgCategoryMapper productImgCategoryMapper;

    /**
     * 用于商品页面关联商品
     * @param q
     * @return
     */
    public List<ProductImgUnion> productImgComgrid(String q) {
        log.info("ProductImgService productImgComgrid q={}", q);
        List<ProductImgUnion> list = productImgMapper.productImgComgrid(q);
        log.info("ProductImgService productImgComgrid return list={}", list);
        return list;
    }

    public EasyUIResult queryImgPage(ProductImgInput productImgInput) {
        PageHelper.startPage(productImgInput.getPageNo(),productImgInput.getPageSize());
        List<ProductImg> list = productImgMapper.selectDynamic(productImgInput);
        log.info("ProductImgService queryImgPage list={}", list);
        PageInfo<ProductImg> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public void updateImgName(ProductImg productImg) {
        productImgMapper.updateByPrimaryKeySelective(productImg);
    }

    public void deleteImg(String ids,String realPath) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            ProductImg productImg = productImgMapper.selectByPrimaryKey(id);
            String filePath = realPath + StringUtils.replace(productImg.getAddress().substring(1), "/", "\\");
            File file = new File(filePath);
            if(file.exists()){
                file.delete();
            }
            productImgMapper.deleteByPrimaryKey(id);
        }
    }


    public EasyUIResult queryImgCategoryPage(ProductImgInput productImgInput) {
        PageHelper.startPage(productImgInput.getPageNo(),productImgInput.getPageSize());
        List<ProductImgCategory> list = productImgCategoryMapper.selectAll();
        log.info("ProductImgService queryImgCategoryPage list={}", list);
        PageInfo<ProductImgCategory> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public void addImgCategory(ProductImgCategory productImgCategory) {
        ProductImgCategory oldProductImgCategory = new ProductImgCategory();
        oldProductImgCategory.setCategory(productImgCategory.getCategory());
        List<ProductImgCategory> list = productImgCategoryMapper.select(oldProductImgCategory);
        if(!list.isEmpty()){
            throw new ServiceException("类目不能用同样（英文）名称");
        }
        productImgCategory.setBelongFile("images");
        productImgCategory.setCreateTime(new Date());
        productImgCategoryMapper.insert(productImgCategory);
    }

    public List<ProductImgCategory> imgCategoryComgrid(String q) {
        log.info("ProductImgService imgCategoryComgrid q={}", q);
        List<ProductImgCategory> list = productImgCategoryMapper.imgCategoryComgrid(q);
        log.info("ProductImgService imgCategoryComgrid return list={}", list);
        return list;
    }



}
