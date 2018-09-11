/**
 * Copyright (C) 2010-2016 Alibaba Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cailanzi.MQTT;

/**
 * MQ 配置
 */
public class MqConfig {
    /**
     * 启动测试之前请替换如下 XXX 为您的配置
     */
    public static final String TOPIC = "cailanzi_order_add";
    public static final String PRODUCER_ID = "PID_cailanzi";
    public static final String CONSUMER_ID = "CID_cailanzi";
    public static final String ORDER_TOPIC = "cailanzi_order";
    public static final String ORDER_PRODUCER_ID = "PID_cailanzi";
    public static final String ORDER_CONSUMER_ID = "CID_cailanzi";
    public static final String ACCESS_KEY = "LTAINnPH4xgT4K3x";
    public static final String SECRET_KEY = "qFNveM7SCpId81ma5Q3hDOiBAk4JDX";
    public static final String TAG = "mq_test_tag";
    /**
     * ONSADDR 请根据不同Region进行配置
     * 公网测试: http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet
     * 公有云生产: http://onsaddr-internal.aliyun.com:8080/rocketmq/nsaddr4client-internal
     * 杭州金融云: http://jbponsaddr-internal.aliyun.com:8080/rocketmq/nsaddr4client-internal
     * 深圳金融云: http://mq4finance-sz.addr.aliyun.com:8080/rocketmq/nsaddr4client-internal
     */
    public static final String ONSADDR = "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet";
}
