package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Async.OrderAsync;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.*;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.*;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import jdk.nashorn.internal.ir.ReturnNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TransferQueue;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductOrderJdMapper productOrderJdMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderShopMapper orderShopMapper;
    @Autowired
    private OrderJdMapper orderJdMapper;

    public SysResult getWebOrderList(OrderListInput orderListInput) throws Exception {
        if(StringUtils.isBlank(orderListInput.getUsername())||StringUtils.isBlank(orderListInput.getBelongStationNo())
                ||StringUtils.isBlank(orderListInput.getType())){
            return SysResult.build(400);
        }
        if(StringUtils.isNotBlank(orderListInput.getEndTime())){
            orderListInput.setEndTime(orderListInput.getEndTime()+" 23:59:59");
        }
        return getWebOrderShopListBasic(orderListInput,ConstantsUtil.Status.READY);
    }

    public SysResult getWebOrder2List(OrderListInput orderListInput) {
        return getWebOrderShopListBasic(orderListInput,ConstantsUtil.Status.DELIVERY);
    }

    public SysResult getWebOrder3List(OrderListInput orderListInput) {
        return getWebOrderShopListBasic(orderListInput,ConstantsUtil.Status.DELIVERY_TO);
    }

    public SysResult getWebOrder4List(OrderListInput orderListInput) {
        if(StringUtils.isNotBlank(orderListInput.getStartTime())){
            orderListInput.setEndTime(orderListInput.getStartTime()+" 23:59:59");
        }
        return getWebOrderShopListBasic(orderListInput,ConstantsUtil.Status.FINISH);
    }

    public SysResult getWebOrder5List(OrderListInput orderListInput) {
        if(StringUtils.isNotBlank(orderListInput.getStartTime())){
            orderListInput.setEndTime(orderListInput.getStartTime()+" 23:59:59");
        }
        return getWebOrderShopListBasic(orderListInput,ConstantsUtil.Status.QUIT);
    }

    public EasyUIResult getOrderList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderUnion> list = orderMapper.getOrderList(orderListInput);
        PageInfo<OrderUnion> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult getOrderShopList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderShop> list = orderShopMapper.getOrderShopPageList(orderListInput);
        PageInfo<OrderShop> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public SysResult updateOrderStatusToDelivery(OrderListInput orderListInput) {
        orderListInput.setUpdateTime(new Date());
        //将当前用户的该订单数据状态全改为待配送
        orderShopMapper.updateOrderStatus(orderListInput);
        //将当前用户的该订单商品为 待发货 ->待配送
        orderShopMapper.updateProductStatusToDelivery(orderListInput);
        productMapper.updateProductStatusEqualOrderShop(orderListInput);
        checkProductsOfOrderIsAllReady(orderListInput);
        return SysResult.build(200);
    }

    private void checkProductsOfOrderIsAllReady(OrderListInput orderListInput) {
        String orderId = orderListInput.getOrderId();
        ProductOrderJd productOrderJd = new ProductOrderJd();
        productOrderJd.setOrderId(orderId);
        List<ProductOrderJd> productOrderJdList = productOrderJdMapper.select(productOrderJd);

        boolean flag = true;
        for (ProductOrderJd orderJd : productOrderJdList) {
            if(orderJd.getSkuStatus()==0){
                flag = false;
                break;
            }
        }
        if(flag){
            orderJdMapper.updateOrderStatusByOrderId(orderId,ConstantsUtil.Status.DELIVERY);
        }
    }

    public SysResult updateProductToStockout(OrderListInput orderListInput) {
        orderListInput.setSkuStatus("2");
        orderJdMapper.updateProductStatus(orderListInput);
        orderListInput.setUpdateTime(new Date());
        orderShopMapper.updateProductStatus(orderListInput);
        return SysResult.build(200);
    }

    private SysResult getWebOrderShopListBasic(OrderListInput orderListInput,String orderStatus) {
        orderListInput.setOrderStatus(orderStatus);
        List<OrderUnion> list = null;
        if(ConstantsUtil.UserType.READYER.equals(orderListInput.getType())){
            list = orderShopMapper.getOrderShopList(orderListInput);
        }else if(ConstantsUtil.UserType.SENDER.equals(orderListInput.getType())){
            list = orderMapper.getOrderShopJdList(orderListInput);
        } else {
            list = new ArrayList<>();
        }
        Map<String,OrderJdVo> map = getOrderJdVoMap(list);
        Collection<OrderJdVo> collection = map.values();
        int total = 0;
        if(ConstantsUtil.Status.FINISH.equals(orderStatus)){
            for(OrderJdVo orderJdVo: collection){
                total += orderJdVo.getCostTotal();
            }
        }
        return SysResult.ok(collection,total);
    }

    private Map<String,OrderJdVo> getOrderJdVoMap(List<OrderUnion> list) {
        Map<String,OrderJdVo> map = new HashMap<>();
        for (OrderUnion union : list) {
            String orderId = union.getOrderId();
            ProductOrderJdVo productOrderJdVo = getProductOrderJdVo(union);
            if(map.containsKey(orderId)){
                OrderJdVo temp = map.get(orderId);
                temp.setCostTotal(temp.getCostTotal()+productOrderJdVo.getCost());
                temp.getProduct().add(productOrderJdVo);
            }else {
                List<ProductOrderJdVo> productOrderJdVoList = new ArrayList<>();
                productOrderJdVoList.add(productOrderJdVo);

                OrderJdVo temp = new OrderJdVo();
                temp.setCreateTime(union.getCreateTime());
                temp.setOrderId(orderId);
                temp.setOrderNum(union.getOrderNum());
                temp.setOrderBuyerRemark(union.getOrderBuyerRemark());
                temp.setProduct(productOrderJdVoList);
                temp.setCostTotal(productOrderJdVo.getCost());

                map.put(orderId,temp);
            }
        }
        return map;
    }

    private ProductOrderJdVo getProductOrderJdVo(OrderUnion union) {
        ProductOrderJdVo productOrderJdVo = new ProductOrderJdVo();
        productOrderJdVo.setSkuId(union.getSkuId()+"");
        productOrderJdVo.setSkuName(union.getSkuName());
        productOrderJdVo.setSkuCount(union.getSkuCount());
        productOrderJdVo.setSkuStorePrice(union.getSkuStorePrice());
        productOrderJdVo.setStatus(union.getSkuStatus()+"");
        productOrderJdVo.setImgUrl(union.getImgUrl());
        try {
            productOrderJdVo.setCost(Integer.parseInt(union.getSkuCount())*Integer.parseInt(union.getSkuStorePrice()));
        }catch (Exception e){
            log.info("计算商品总价转换异常：",e);
        }
        return productOrderJdVo;
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
}
