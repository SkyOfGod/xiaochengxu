package com.cailanzi.rabbitmq;

import lombok.Data;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Data
public class MqOrder {

    private String billId;
    private String statusId;
    private String timestamp;

}
