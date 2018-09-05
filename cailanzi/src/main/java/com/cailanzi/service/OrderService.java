package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Async.OrderAsync;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.*;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.OrderJd;
import com.cailanzi.pojo.entities.OrderShop;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ShopJd;
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

import java.util.*;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderAsync orderAsync;
    @Autowired
    private OrderShopMapper orderShopMapper;
    @Autowired
    private OrderJdMapper orderJdMapper;

    public SysResult getWebOrderList(OrderListInput orderListInput) throws Exception {
        if(StringUtils.isBlank(orderListInput.getUsername())||StringUtils.isBlank(orderListInput.getBelongStationNo())
                ||StringUtils.isBlank(orderListInput.getType())){
            return SysResult.build(400);
        }
        String result = getOrderListResultData(orderListInput);
        JSONObject resultJson = JSON.parseObject(result);
        JSONArray jsonArray = resultJson.getJSONArray("resultList");

        List<OrderJdVo> list = getInitOrderJdVoList(jsonArray,orderListInput);
        try {
            if(list.isEmpty()){
                return SysResult.ok(list);
            }
            if(ConstantsUtil.UserType.SENDER.equals(orderListInput.getType())){//收货员
                return SysResult.ok(list);
            }else if(ConstantsUtil.UserType.READYER.equals(orderListInput.getType())) {//拣货员
                //获取账号对应的配置产品skuId
                Set<String> skuIds = getContainSkuIds(orderListInput);
                if(skuIds.isEmpty()){
                    return SysResult.ok(new ArrayList<OrderJdVo>());
                }
                //获取order_shop中不在待发货中订单id
                Set<String> orderIds = getOutReadyStatusOrderIdsOfOrderShop(list,orderListInput.getUsername());
                List<OrderJdVo> newList = new ArrayList<>();
                for (OrderJdVo orderJdVo : list) {
                    if(orderIds.contains(orderJdVo.getOrderId())){
                        continue;
                    }
                    List<ProductOrderJdVo> newProducts = new ArrayList<>();
                    List<ProductOrderJdVo> products = orderJdVo.getProduct();
                    for (ProductOrderJdVo productOrderJdVo : products) {
                        if(skuIds.contains(productOrderJdVo.getSkuId())){
                            newProducts.add(productOrderJdVo);
                        }
                    }
                    if(!newProducts.isEmpty()){
                        orderJdVo.setProduct(newProducts);
                        newList.add(orderJdVo);
                    }
                }
                if(!newList.isEmpty()){
                    orderAsync.insertOrderShop(newList,orderListInput);
                }
                return SysResult.ok(newList);
            }
            return null;
        } finally {
            //异步保存订单数据
            if(jsonArray!=null&&!jsonArray.isEmpty()){
                orderAsync.insertOrderJd(jsonArray);
            }
        }
    }

    /**
     * 在order_shop中获取不在待发货状态下的订单
     * @param list
     * @return
     */
    private Set<String> getOutReadyStatusOrderIdsOfOrderShop(List<OrderJdVo> list,String username) {
        Set<String> orderIds = new HashSet<>();
        for (OrderJdVo orderJdVo : list) {
            orderIds.add(orderJdVo.getOrderId());
        }
        //TODO 如果一个订单下的商品状态不一致 待处理
        return orderShopMapper.getOutReadyStatusOrderIdsOfOrderShop(username,orderIds,ConstantsUtil.Status.READY);
    }

    private List<OrderJdVo> getInitOrderJdVoList(JSONArray jsonArray,OrderListInput orderListInput) {
        List<OrderJdVo> list = new ArrayList<>();
        if(jsonArray!=null&&!jsonArray.isEmpty()){
            Set<String> orderIds = getOutReadyStatusOrderIdsOfOrderJd(jsonArray);
            for (Object orderObj : jsonArray) {
                JSONObject orderJsonObject = JSON.parseObject(orderObj.toString());
                String orderId = orderJsonObject.getString("orderId");
                if(orderIds.contains(orderId)){
                    continue;
                }
                JSONArray proJSonArray = orderJsonObject.getJSONArray("product");
                List<ProductOrderJdVo> productOrderJdVoList = new ArrayList<>();
                for (Object orderProductObj : proJSonArray) {
                    JSONObject orderProductJsonObject = JSON.parseObject(orderProductObj.toString());
                    ProductOrderJdVo productOrderJdVo = getOrderProductVoList(orderProductJsonObject);
                    productOrderJdVoList.add(productOrderJdVo);
                }
                if(ConstantsUtil.UserType.SENDER.equals(orderListInput.getType())){
                    this.handleProductOrderJdStatusFromOrderShop(productOrderJdVoList,orderId);
                }
                OrderJdVo orderJdVo = new OrderJdVo();
                orderJdVo.setOrderId(orderId);
                orderJdVo.setOrderNum(orderJsonObject.getInteger("orderNum"));
                orderJdVo.setProduct(productOrderJdVoList);
                list.add(orderJdVo);
            }
        }
        return list;
    }

    /**
     * 跟新收货员订单下商品备货状态
     * @param productOrderJdVoList
     * @param orderId
     */
    private void handleProductOrderJdStatusFromOrderShop(List<ProductOrderJdVo> productOrderJdVoList, String orderId) {
        Set<String> skuIds = new HashSet<>();
        for (ProductOrderJdVo productOrderJdVo : productOrderJdVoList) {
            skuIds.add(productOrderJdVo.getSkuId());
        }
        List<OrderShop> list = orderShopMapper.getProductStatus(orderId,skuIds);
        Map<String,String> map = new HashMap<>();
        for (OrderShop orderShop : list) {
            map.put(orderShop.getSkuId()+"",orderShop.getStatus());
        }
        for (ProductOrderJdVo productOrderJdVo : productOrderJdVoList) {
            if(map.containsKey(productOrderJdVo.getSkuId())){
                productOrderJdVo.setStatus(map.get(productOrderJdVo.getSkuId()));
            }else {
                productOrderJdVo.setStatus(ConstantsUtil.Status.READY);
            }
        }
    }

    /**
     * 查询order_jd中订单不在待发货的订单
     * @param jsonArray
     * @return
     */
    private Set<String> getOutReadyStatusOrderIdsOfOrderJd(JSONArray jsonArray) {
        Set<String> orderIds = new HashSet<>();
        for (Object orderObj : jsonArray) {
            JSONObject orderJsonObject = JSON.parseObject(orderObj.toString());
            orderIds.add(orderJsonObject.getString("orderId"));
        }
        return orderJdMapper.getDeliveryOrderIdsOfOrderJd(orderIds,ConstantsUtil.Status.READY);
    }

    private ProductOrderJdVo getOrderProductVoList(JSONObject orderProductJsonObject) {
        ProductOrderJdVo productOrderJdVo = new ProductOrderJdVo();
        productOrderJdVo.setSkuId(orderProductJsonObject.getString("skuId"));
        productOrderJdVo.setSkuName(orderProductJsonObject.getString("skuName"));
        productOrderJdVo.setSkuCount(orderProductJsonObject.getString("skuCount"));
        productOrderJdVo.setSkuStorePrice(orderProductJsonObject.getString("skuStorePrice"));
        return productOrderJdVo;
    }

    private Set<String> getContainSkuIds(OrderListInput orderListInput){
        Product product = new Product();
        product.setPhone(orderListInput.getUsername());
        product.setBelongStationNo(orderListInput.getBelongStationNo());

        List<Product> productList = productMapper.select(product);
        Set<String> set = new HashSet<>();
        for (Product pro : productList) {
            set.add(pro.getSkuId()+"");
        }
        return set;
    }

    public String getOrderListResultData(OrderListInput orderListInput) throws Exception {
        log.info("OrderService getOrderList OrderInput orderInput={}", orderListInput);
        OrderListInput newOrderListInput = new OrderListInput();
        newOrderListInput.setPageSize(100);
        newOrderListInput.setOrderStatus(ConstantsUtil.Status.READY);
        newOrderListInput.setDeliveryStationNo(orderListInput.getBelongStationNo());

        String url = "https://openo2o.jd.com/djapi/order/es/query";
        String jdParamJson = JdHttpCilentUtil.getJdParamJson(newOrderListInput);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        if(!"0".equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data.getString("result");
    }

    public EasyUIResult getOrderList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderJdVo> list = orderMapper.getOrderList(orderListInput);
        PageInfo<OrderJdVo> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult getOrderShopList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderShop> list = orderShopMapper.getOrderShopPageList(orderListInput);
        PageInfo<OrderShop> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public SysResult updateOrderShopStatus(OrderListInput orderListInput) {
        orderListInput.setUpdateTime(new Date());
        orderShopMapper.updateOrderShopStatus(orderListInput);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
        orderAsync.checkProductsOfOrderIsAllReady(orderListInput);
        return SysResult.build(200);
    }

    public SysResult getWebOrder2List(OrderListInput orderListInput) {
        String username = orderListInput.getUsername();
        String stationNo = orderListInput.getBelongStationNo();
        List<OrderUnion> list = null;
        if(ConstantsUtil.UserType.READYER.equals(orderListInput.getType())){
            list = orderShopMapper.getOrderShopList(username,stationNo,ConstantsUtil.Status.DELIVERY);
        }else if(ConstantsUtil.UserType.SENDER.equals(orderListInput.getType())){
            list = orderMapper.getOrderShopJdList(stationNo,ConstantsUtil.Status.DELIVERY);
        }
        Map<String,OrderJdVo> map = getOrderJdVoMap(list);
        return SysResult.ok(map.values());
    }

    private Map<String,OrderJdVo> getOrderJdVoMap(List<OrderUnion> list) {
        Map<String,OrderJdVo> map = new HashMap<>();
        for (OrderUnion union : list) {
            String orderId = union.getOrderId();
            if(map.containsKey(orderId)){
                OrderJdVo temp = map.get(orderId);
                List<ProductOrderJdVo> productOrderJdVoList = temp.getProduct();
                productOrderJdVoList.add(getProductOrderJdVo(union));
            }else {
                OrderJdVo temp = new OrderJdVo();
                temp.setOrderId(orderId);
                temp.setOrderNum(union.getOrderNum());
                List<ProductOrderJdVo> productOrderJdVoList = new ArrayList<>();
                productOrderJdVoList.add(getProductOrderJdVo(union));
                temp.setProduct(productOrderJdVoList);

                map.put(orderId,temp);
            }
        }
        return map;
    }

    private ProductOrderJdVo getProductOrderJdVo(OrderUnion union) {
        ProductOrderJdVo productOrderJdVo = new ProductOrderJdVo();
        productOrderJdVo.setSkuName(union.getSkuName());
        productOrderJdVo.setSkuCount(union.getSkuCount());
        productOrderJdVo.setSkuStorePrice(union.getSkuStorePrice());
        return productOrderJdVo;
    }
}
