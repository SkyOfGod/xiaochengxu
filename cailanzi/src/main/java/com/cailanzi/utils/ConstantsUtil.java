package com.cailanzi.utils;

/**
 * Created by v-hel27 on 2018/9/5.
 */
public interface ConstantsUtil {

    /**
     * 订单状态：32000-待发货，33000-待配送，34000-配送中，35000-已完成
     */
    public static class Status {

        public final static String READY = "32000";
        public final static String DELIVERY = "33000";
        public final static String FINISH = "35000";
    }

    /**
     * 用户类型：0-后台管理员，1-备货员，2-收货员/送货员
     */
    public static class UserType {

        public final static String MANAGER = "0";
        public final static String READYER = "1";
        public final static String SENDER = "2";

    }

}