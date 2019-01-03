package com.cailanzi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdResult {

    //0	操作成功 -1	操作失败 -10000	业务重试 10005	必填项参数未填
    // 10013 无效Token令牌 10014 无效Sign签名 10015	API参数异常 10018 不存在的方法名
    private String code;

    private String msg;

    private Object data;

    public static JdResult build(String code, String msg, Object data) {
        return new JdResult(code, msg, data);
    }

    public static JdResult ok(Object data) {
        return new JdResult("0", "success", data);
    }

}
