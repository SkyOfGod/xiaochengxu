package com.cailanzi.rabbitMQ.messageNotify.pojo;

import com.cailanzi.rabbitMQ.messageNotify.pojo.MqOrder;
import lombok.Data;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Data
public class JdOrderImport {

    private String token;
    private String app_key;
    private String timestamp;
    private String format;
    private String v;
    private String sign;
    private MqOrder jd_param_json;
}
