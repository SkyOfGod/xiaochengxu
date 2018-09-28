package com.cailanzi.pojo;

import lombok.Data;

@Data
public class PicUploadResult {
    
    private Integer error;
    
    private String url;
    
    private String width;
    
    private String height;
    /*
     * 200	成功
     * 201	错误
     * 400	参数错误
     */
    private Integer status;

    private String msg;

    

}
