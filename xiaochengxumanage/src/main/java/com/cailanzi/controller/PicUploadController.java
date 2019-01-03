package com.cailanzi.controller;

import com.cailanzi.mapper.ProductImgCategoryMapper;
import com.cailanzi.mapper.ProductImgMapper;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.ProductImg;
import com.cailanzi.pojo.entities.ProductImgCategory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;


/**
 * 图片上传
 */
@Slf4j
@RestController
@RequestMapping("/pic")
public class PicUploadController {

    @Autowired
    private ProductImgMapper productImgMapper;
    @Autowired
    private ProductImgCategoryMapper productImgCategoryMapper;

    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[] { ".jpg", ".bmp", ".jpeg", ".gif", ".png" };

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public SysResult upload(@RequestParam("file") MultipartFile uploadFile, @RequestParam("categoryId") String categoryId,
                            HttpServletRequest request) throws Exception {
        // 校验图片格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            return SysResult.build(201,"图片后缀只能为[.bmp .jpg .jpeg .gif .png");
        }

        // 文件新路径
        String filePath = getFilePath(uploadFile.getOriginalFilename(),categoryId,request.getServletContext().getRealPath("/"));

        if (log.isDebugEnabled()) {
            log.debug("Pic file upload .[{}] to [{}] .", uploadFile.getOriginalFilename(), filePath);
        }
        File newFile = new File(filePath);
        // 写文件到磁盘
        uploadFile.transferTo(newFile);

        return SysResult.build(200);
    }

    private String getFilePath(String sourceFileName,String categoryId,String realPath) {
        ProductImgCategory productImgCategory = productImgCategoryMapper.selectByPrimaryKey(categoryId);
        String fileFolder = realPath + productImgCategory.getBelongFile()+ File.separator + productImgCategory.getCategory();
        File file = new File(fileFolder);
        if (!file.isDirectory()) {
            // 如果目录不存在，则创建目录
            file.mkdirs();
        }

        String name = sourceFileName.substring(0,sourceFileName.lastIndexOf("."));
        String suffix = sourceFileName.substring(sourceFileName.lastIndexOf("."));
        ProductImg productImg = new ProductImg();
        productImg.setCategoryId(Integer.parseInt(categoryId));
        productImg.setName(name);
        productImg.setCreateTime(new Date());
        productImgMapper.insert(productImg);
        //比如：/images/fish/1.jpg
        String address = "/" + productImgCategory.getBelongFile()+ "/" + productImgCategory.getCategory()+ "/" +productImg.getId()+suffix;
        productImg.setAddress(address);
        productImgMapper.updateByPrimaryKey(productImg);
        // 生成新的文件名
        return fileFolder + File.separator +productImg.getId()+suffix;
    }


}
