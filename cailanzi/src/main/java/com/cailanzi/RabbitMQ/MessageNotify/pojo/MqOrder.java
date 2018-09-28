package com.cailanzi.rabbitMQ.messageNotify.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Data
public class MqOrder{

    private String billId;
    private String statusId;
    private String timestamp;

}
