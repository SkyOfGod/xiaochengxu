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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.ReusableMessage;
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
        JSONObject orderJsonObject = JSON.parseObject(jsonArray.get(0).toString());
        String orderId = orderJsonObject.getString("orderId");
        String orderStatus = getOrderStatus(orderJsonObject);

        List<ProductOrderJd> productList = new ArrayList<>();
        JSONArray proJSonArray = orderJsonObject.getJSONArray("product");
        for (Object orderProductObj : proJSonArray) {
            JSONObject orderProductJsonObject = JSON.parseObject(orderProductObj.toString());
            productList.add(getProductOrderJd(orderId,orderProductJsonObject));
        }
        if(!productList.isEmpty()){
            OrderJd orderJd = getOrderJd(orderJsonObject,orderStatus);
            log.info("OrderAsync insertOrderJd OrderJd orderJd={}", orderJd);
            orderJdMapper.insert(orderJd);
            log.info("OrderAsync insertOrderJd insertList List<ProductOrderJd> productList={}", productList);
            productOrderJdMapper.insertList(productList);

            insertOrderShop(orderId,orderStatus);
        }
    }

    private String getOrderStatus(JSONObject orderJsonObject) {
        String status = ConstantsUtil.Status.READY;
        String orderStatus = orderJsonObject.getString("orderStatus");
        if("33040".equals(orderStatus)){
            status = ConstantsUtil.Status.DELIVERY_TO;
        }else if("33060".equals(orderStatus)){
            status = ConstantsUtil.Status.FINISH;
        }else if("20020".equals(orderStatus)){
            status = ConstantsUtil.Status.QUIT;
        }
        return status;
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

    private OrderJd getOrderJd(JSONObject orderJsonObject,String status) {
        String orderPreStartDeliveryTime = orderJsonObject.getString("orderPreStartDeliveryTime");
        String orderPreEndDeliveryTime = orderJsonObject.getString("orderPreEndDeliveryTime");
        String businessTag = orderJsonObject.getString("businessTag");
        String temp = "[立即送达]";
        if(businessTag.contains("one_dingshida")){//定时达
            temp = "[请于"+orderPreStartDeliveryTime.substring(0,16)+" ~ "+orderPreEndDeliveryTime.substring(11,16)+" 送达]";
        }

        OrderJd orderJd = JSONObject.toJavaObject(orderJsonObject,OrderJd.class);
        orderJd.setStatus(status);
        orderJd.setCreateTime(new Date());
        orderJd.setUpdateTime(orderJd.getCreateTime());
        if(StringUtils.isBlank(orderJd.getOrderBuyerRemark())){
            orderJd.setOrderBuyerRemark(temp);
        }else {
            orderJd.setOrderBuyerRemark(orderJd.getOrderBuyerRemark()+temp);
        }
        return orderJd;
    }

    public void insertOrderShop(String orderId,String orderStatus) {
        Date date = new Date();
        List<OrderShop> inList = new ArrayList<>();

        List<OrderShop> list = orderShopMapper.getOrderShopListInitData(orderId);
        for (OrderShop orderShop : list) {
            String username = orderShop.getUsername();
            if(isExitOrderShop(orderId,username)){
                continue;
            }
            orderShop.setCreateTime(date);
            orderShop.setUpdateTime(date);
            orderShop.setSkuStatus(ConstantsUtil.ProductStatus.READY);
            orderShop.setOrderStatus(orderStatus);
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
