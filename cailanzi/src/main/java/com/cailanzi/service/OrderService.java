package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Async.OrderAsync;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.OrderMapper;
import com.cailanzi.mapper.OrderShopMapper;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.ShopMapper;
import com.cailanzi.pojo.*;
import com.cailanzi.pojo.entities.OrderShop;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ShopJd;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    public SysResult getWebOrderList(OrderListInput orderListInput) throws Exception {
        if(StringUtils.isBlank(orderListInput.getUsername())||StringUtils.isBlank(orderListInput.getBelongStationNo())){
            return SysResult.build(400);
        }
        String result = getOrderListResultData(orderListInput);
        JSONObject resultJson = JSON.parseObject(result);
        JSONArray jsonArray = resultJson.getJSONArray("resultList");

        List<OrderJdVo> list = new ArrayList<>();
        try {
            if(jsonArray==null||jsonArray.isEmpty()){
                return SysResult.ok(list);
            }
            //门店订单数据直接返回
            if("0".equals(orderListInput.getBelongStationNo())){
                return SysResult.ok(resultJson.get("resultList"));
            }
            //获取账号对应的配置产品skuId
            Set<Long> skuIds = getContainSkuIds(orderListInput);
            if(skuIds.isEmpty()){
                return SysResult.ok(list);
            }

            for (Object orderObj : jsonArray) {
                JSONObject orderJsonObject = JSON.parseObject(orderObj.toString());

                JSONArray proJSonArray = orderJsonObject.getJSONArray("product");
                List<ProductOrderJdVo> productOrderJdVoList = new ArrayList<>();
                for (Object orderProductObj : proJSonArray) {
                    JSONObject orderProductJsonObject = JSON.parseObject(orderProductObj.toString());
                    Long skuId = orderProductJsonObject.getLong("skuId");
                    if(skuIds.contains(skuId)){
                        ProductOrderJdVo productOrderJdVo = getOrderProductVoList(orderProductJsonObject);
                        productOrderJdVoList.add(productOrderJdVo);
                    }
                }
                if(!productOrderJdVoList.isEmpty()){
                    OrderJdVo orderJdVo = new OrderJdVo();
                    orderJdVo.setOrderId(orderJsonObject.getString("orderId"));
                    orderJdVo.setOrderNum(orderJsonObject.getInteger("orderNum"));
                    orderJdVo.setProduct(productOrderJdVoList);
                    list.add(orderJdVo);
                }
            }
        } finally {
            //异步保存订单数据
            if(jsonArray!=null&&!jsonArray.isEmpty()){
                orderAsync.insertOrderJd(jsonArray);
                if(!list.isEmpty()){
                    orderAsync.insertOrderShop(list,orderListInput);
                }
            }
        }

        return SysResult.ok(list);
    }

    private ProductOrderJdVo getOrderProductVoList(JSONObject orderProductJsonObject) {
        ProductOrderJdVo productOrderJdVo = new ProductOrderJdVo();
        productOrderJdVo.setSkuId(orderProductJsonObject.getString("skuId"));
        productOrderJdVo.setSkuName(orderProductJsonObject.getString("skuName"));
        productOrderJdVo.setSkuCount(orderProductJsonObject.getString("skuCount"));
        productOrderJdVo.setSkuStorePrice(orderProductJsonObject.getString("skuStorePrice"));
        return productOrderJdVo;
    }

    private Set<Long> getContainSkuIds(OrderListInput orderListInput){
        Product product = new Product();
        product.setPhone(orderListInput.getUsername());
        product.setBelongStationNo(orderListInput.getBelongStationNo());

        List<Product> productList = productMapper.select(product);
        Set<Long> set = new HashSet<>();
        for (Product pro : productList) {
            set.add(pro.getSkuId());
        }
        return set;
    }

    public String getOrderListResultData(OrderListInput orderListInput) throws Exception {
        log.info("OrderService getOrderList OrderInput orderInput={}", orderListInput);
        OrderListInput newOrderListInput = new OrderListInput();
        newOrderListInput.setPageSize(100);
        newOrderListInput.setOrderStatus("32000");
        if("0".equals(orderListInput.getBelongStationNo())){
            ShopJd shopJd = new ShopJd();
            shopJd.setPhone(orderListInput.getUsername());
            ShopJd newShop = shopMapper.selectOne(shopJd);
            newOrderListInput.setDeliveryStationNo(newShop.getStationNo());
        }else {
            newOrderListInput.setDeliveryStationNo(orderListInput.getBelongStationNo());
        }

        String url = "https://openo2o.jd.com/djapi/order/es/query";
        String jdParamJson = JdHttpCilentUtil.getJdParamJson(newOrderListInput);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        if(!"0".equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data.getString("result");
    }

    /*public static void main(String[] args) throws UnsupportedEncodingException, IllegalAccessException {
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId("1");
        orderListInput.setPageNo("2");
        orderListInput.setPageSize("    ");

        Class clz = orderListInput.getClass();
        Map<String,Object> map = new HashMap<>();
        for (Field field : clz.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.get(orderListInput)!=null&&StringUtils.isNotBlank(field.get(orderListInput).toString())){
                map.put(field.getName(),field.get(orderListInput));
            }
        }
        System.out.println(JSON.toJSONString(map));
    }*/

    public EasyUIResult getOrderList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderJdVo> list = orderMapper.getOrderList(orderListInput);
        PageInfo<OrderJdVo> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public EasyUIResult getOrderShopList(OrderListInput orderListInput) {
        PageHelper.startPage(orderListInput.getPageNo(),orderListInput.getPageSize());
        List<OrderShop> list = orderShopMapper.selectAll();
        PageInfo<OrderShop> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
