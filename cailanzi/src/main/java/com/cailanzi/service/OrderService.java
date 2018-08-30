package com.cailanzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.ShopMapper;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.OrderProductVo;
import com.cailanzi.pojo.OrderVo;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.ShopJd;
import com.cailanzi.utils.JdHttpCilentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
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

    public SysResult getOrderList(OrderListInput orderListInput) throws Exception {
        if(StringUtils.isBlank(orderListInput.getUsername())||StringUtils.isBlank(orderListInput.getBelongStationNo())){
            return SysResult.build(400);
        }
        String result = getOrderListResultData(orderListInput);
        JSONObject resultJson = JSON.parseObject(result);
        if("0".equals(orderListInput.getBelongStationNo())){
            return SysResult.ok(resultJson.get("resultList"));
        }
        //获取账号对应的配置产品skuId
        Set<Long> skuIds = getContainSkuIds(orderListInput);
        List<OrderVo> list = new ArrayList<>();
        if(skuIds.isEmpty()){
            return SysResult.ok(list);
        }

        JSONArray jsonArray = resultJson.getJSONArray("resultList");
        if(jsonArray==null||jsonArray.isEmpty()){
            return SysResult.ok(list);
        }
        for (Object orderObj : jsonArray) {
            JSONObject orderJsonObject = JSON.parseObject(orderObj.toString());

            JSONArray proJSonArray = orderJsonObject.getJSONArray("product");
            List<OrderProductVo> orderProductVoList = new ArrayList<>();
            for (Object orderProductObj : proJSonArray) {
                JSONObject orderProductJsonObject = JSON.parseObject(orderProductObj.toString());
                Long skuId = orderProductJsonObject.getLong("skuId");
                if(skuIds.contains(skuId)){
                    OrderProductVo orderProductVo = new OrderProductVo();
                    orderProductVo.setSkuName(orderProductJsonObject.getString("skuName"));
                    orderProductVo.setSkuCount(orderProductJsonObject.getString("skuCount"));
                    orderProductVo.setSkuStorePrice(orderProductJsonObject.getString("skuStorePrice"));
                    orderProductVoList.add(orderProductVo);
                }
            }
            if(!orderProductVoList.isEmpty()){
                OrderVo orderVo = new OrderVo();
                orderVo.setOrderId(orderJsonObject.getString("orderId"));
                orderVo.setProduct(orderProductVoList);
                list.add(orderVo);
            }
        }
        return SysResult.ok(list);
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
        newOrderListInput.setPageSize("100");
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

    public static void main(String[] args) throws UnsupportedEncodingException, IllegalAccessException {
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
    }


}
