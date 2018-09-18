package com.cailanzi.Async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.*;
import com.cailanzi.pojo.OrderJdVo;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.ProductOrderJdVo;
import com.cailanzi.pojo.entities.OrderJd;
import com.cailanzi.pojo.entities.OrderShop;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ProductOrderJd;
import com.cailanzi.utils.ConstantsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Component
@Slf4j
public class OrderAsync {

    @Autowired
    private OrderJdMapper orderJdMapper;
    @Autowired
    private ProductOrderJdMapper productOrderJdMapper;
    @Autowired
    private OrderShopMapper orderShopMapper;

//    @Async
    public void insertOrderJd(JSONArray jsonArray) {
        Date date = new Date();

        JSONObject orderJsonObject = JSON.parseObject(jsonArray.get(0).toString());
        String orderId = orderJsonObject.getString("orderId");

        List<ProductOrderJd> productList = new ArrayList<>();
        JSONArray proJSonArray = orderJsonObject.getJSONArray("product");
        for (Object orderProductObj : proJSonArray) {
            JSONObject orderProductJsonObject = JSON.parseObject(orderProductObj.toString());
            productList.add(getProductOrderJd(orderId,orderProductJsonObject));
        }
        if(!productList.isEmpty()){
            OrderJd orderJd = getOrderJd(date,orderJsonObject);
            log.info("OrderAsync insertOrderJd OrderJd orderJd={}", orderJd);
            orderJdMapper.insert(orderJd);
            log.info("OrderAsync insertOrderJd insertList List<ProductOrderJd> productList={}", productList);
            productOrderJdMapper.insertList(productList);

            insertOrderShop(orderId);
        }
    }

    public boolean isExitOrder(String orderId) {
        OrderJd orderJd = new OrderJd();
        orderJd.setOrderId(orderId);
        List<OrderJd> list = orderJdMapper.select(orderJd);
        if(!list.isEmpty()){
            return true;
        }
        return false;
    }

    private ProductOrderJd getProductOrderJd(String orderId, JSONObject orderProductJsonObject) {
        ProductOrderJd productOrderJd = JSONObject.toJavaObject(orderProductJsonObject,ProductOrderJd.class);
        productOrderJd.setOrderId(orderId);
        productOrderJd.setSkuStatus((byte)0);
        return productOrderJd;
    }

    private OrderJd getOrderJd(Date date,JSONObject orderJsonObject) {
        OrderJd orderJd = JSONObject.toJavaObject(orderJsonObject,OrderJd.class);
        orderJd.setStatus(ConstantsUtil.Status.READY);
        orderJd.setCreateTime(date);
        return orderJd;
    }

    public void insertOrderShop(String orderId) {
        Date date = new Date();
        List<OrderShop> inList = new ArrayList<>();

        List<OrderShop> list = orderShopMapper.getOrderShopListInitData(orderId);
        for (OrderShop orderShop : list) {
            String username = orderShop.getUsername();
            if(isExitOrderShop(orderId,username)){
                continue;
            }
            orderShop.setOrderId(orderId);
            orderShop.setCreateTime(date);
            orderShop.setUpdateTime(date);
            orderShop.setSkuStatus(ConstantsUtil.ProductStatus.READY);
            orderShop.setOrderStatus(ConstantsUtil.Status.READY);
            inList.add(orderShop);
        }
        if(!inList.isEmpty()){
            log.info("OrderAsync insertOrderShop insertList List<OrderShop> inList={}", inList);
            orderShopMapper.insertList(inList);
        }
    }

    private boolean isExitOrderShop(String orderId, String username) {
        OrderShop orderShop = new OrderShop();
        orderShop.setOrderId(orderId);
        orderShop.setUsername(username);
        List<OrderShop> list = orderShopMapper.select(orderShop);
        if(!list.isEmpty()){
            return true;
        }
        return false;
    }

}
