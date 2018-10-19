package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.*;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.OrderJd;
import com.cailanzi.pojo.entities.OrderShop;
import com.cailanzi.pojo.entities.ProductOrderJd;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private ProductOrderJdMapper productOrderJdMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderShopMapper orderShopMapper;
    @Autowired
    private OrderJdMapper orderJdMapper;

    public EasyUIResult getOrderList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderUnion> list = orderJdMapper.getOrderList(orderListInput);
        PageInfo<OrderUnion> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult getOrderProductList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderUnion> list = orderMapper.getOrderProductList(orderListInput);
        PageInfo<OrderUnion> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult getOrderShopList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderShop> list = orderShopMapper.getOrderShopPageList(orderListInput);
        PageInfo<OrderShop> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Transactional
    public SysResult deleteOrder(String orderId) {
        OrderJd orderJd = new OrderJd();
        orderJd.setOrderId(orderId);
        orderJdMapper.delete(orderJd);

        ProductOrderJd productOrderJd = new ProductOrderJd();
        productOrderJd.setOrderId(orderId);
        productOrderJdMapper.delete(productOrderJd);
        return SysResult.build(200);
    }

    /**
     * 从京东获取订单详情
     * @param orderListInput
     * @return
     * @throws Exception
     */
    public String getOrderListResultData(OrderListInput orderListInput) throws Exception {
        log.info("OrderService getOrderListResultData OrderInput orderInput={}", orderListInput);
        String url = "https://openo2o.jd.com/djapi/order/es/query";
        String jdParamJson = JdHttpCilentUtil.getJdParamJson(orderListInput);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        if(!"0".equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data.getString("result");
    }

    /***********************************************/
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
            log.info("OrderService insertOrderJd OrderJd orderJd={}", orderJd);
            orderJdMapper.insert(orderJd);
            log.info("OrderService insertOrderJd insertList List<ProductOrderJd> productList={}", productList);
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
            log.info("OrderService insertOrderShop insertList List<OrderShop> inList={}", inList);
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
