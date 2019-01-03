package com.cailanzi.utils;

/**
 * Created by v-hel27 on 2018/9/5.
 */
public interface ConstantsUtil {

    /**
     * 订单状态：32000-待发货，33000-待配送，34000-配送中，35000-已完成，36000-已取消
     */
    public static class Status {

        public final static String READY = "32000";
//        public final static String DELIVERY = "33000";
        public final static String DELIVERY_TO = "34000";
        public final static String FINISH = "35000";
        public final static String QUIT = "36000";
    }

    /**
     * 商品状态：0-待拣货，1-已拣货，2-缺货
     */
    public static class ProductStatus {

        public final static Byte READY = 0;
//        public final static Byte FINISH = 1;
//        public final static Byte STOCKOUT = 2;
    }


}