package com.cailanzi.Async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.OrderJdMapper;
import com.cailanzi.mapper.OrderShopMapper;
import com.cailanzi.mapper.ProductOrderJdMapper;
import com.cailanzi.pojo.OrderJdVo;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.ProductOrderJdVo;
import com.cailanzi.pojo.entities.OrderJd;
import com.cailanzi.pojo.entities.OrderShop;
import com.cailanzi.pojo.entities.ProductOrderJd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/9/4.
 */
@Component
public class OrderAsync {

    @Autowired
    private OrderJdMapper orderJdMapper;
    @Autowired
    private ProductOrderJdMapper productOrderJdMapper;
    @Autowired
    private OrderShopMapper orderShopMapper;

    private final static String ORDER_READY_STATUS = "32000";

    @Async
    public void insertOrderJd(JSONArray jsonArray) {
        Date date = new Date();
        List<OrderJd> orderList = new ArrayList<>();
        List<ProductOrderJd> productList = new ArrayList<>();
        for (Object orderObj : jsonArray) {
            JSONObject orderJsonObject = JSON.parseObject(orderObj.toString());
            String orderId = orderJsonObject.getString("orderId");
            if(isExitOrder(orderId)){
                continue;
            }
            orderList.add(getOrderJd(date,orderJsonObject));
            JSONArray proJSonArray = orderJsonObject.getJSONArray("product");
            for (Object orderProductObj : proJSonArray) {
                JSONObject orderProductJsonObject = JSON.parseObject(orderProductObj.toString());
                productList.add(getProductOrderJd(orderId,orderProductJsonObject));
            }
        }
        if(!orderList.isEmpty()){
            orderJdMapper.insertList(orderList);
            productOrderJdMapper.insertList(productList);
        }
    }

    private boolean isExitOrder(String orderId) {
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
        return productOrderJd;
    }

    private OrderJd getOrderJd(Date date,JSONObject orderJsonObject) {
        OrderJd orderJd = JSONObject.toJavaObject(orderJsonObject,OrderJd.class);
        orderJd.setStatus(ORDER_READY_STATUS);
        orderJd.setCreateTime(date);
        return orderJd;
    }

    @Async
    public void insertOrderShop(List<OrderJdVo> list, OrderListInput orderListInput) {
        Date date = new Date();
        String username = orderListInput.getUsername();
        String stationNo = orderListInput.getBelongStationNo();

        List<OrderShop> inList = new ArrayList<>();
        for (OrderJdVo orderJdVo : list) {
            String orderId = orderJdVo.getOrderId();
            if(isExitOrderShop(orderId,username)){
                continue;
            }

            List<ProductOrderJdVo> productOrderJdList = orderJdVo.getProduct();
            for (ProductOrderJdVo productOrderJdVo : productOrderJdList) {
                OrderShop orderShop = new OrderShop();
                orderShop.setOrderId(orderId);
                orderShop.setCreateTime(date);
                orderShop.setUpdateTime(date);
                orderShop.setUsername(username);
                orderShop.setBelongStationNo(stationNo);
                orderShop.setStatus(ORDER_READY_STATUS);

                orderShop.setSkuId(Long.parseLong(productOrderJdVo.getSkuId()));
                orderShop.setSkuName(productOrderJdVo.getSkuName());
                orderShop.setSkuCount(Integer.parseInt(productOrderJdVo.getSkuCount()));
                orderShop.setSkuPrice(Integer.parseInt(productOrderJdVo.getSkuStorePrice()));
                inList.add(orderShop);
            }
        }
        if(!inList.isEmpty()){
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
