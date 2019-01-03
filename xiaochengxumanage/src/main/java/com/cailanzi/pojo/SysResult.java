package com.cailanzi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by v-hel27 on 2018/8/11.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysResult {

    /*
     * 200	成功
     * 201	错误
     * 400	参数错误
     */
    private Integer status;

    private String msg;

    private Object data;

    private Integer count;//总价或者总数

    public static SysResult build(Integer status, String msg) {
        return new SysResult(status, msg, null,null);
    }

    public static SysResult build(Integer status) {
        return new SysResult(status, "", null,null);
    }

    public static SysResult ok(Object data) {
        return new SysResult(200,"ok",data,null);
    }
    public static SysResult ok(Object data, Integer count) {
        return new SysResult(200,"ok",data,count);
    }
}
