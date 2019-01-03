package com.cailanzi.rabbitMQ.rabbitListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.async.OrderAsync;
import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.ConfigMapper;
import com.cailanzi.rabbitMQ.messageNotify.pojo.MqOrder;
import com.cailanzi.mapper.OrderJdMapper;
import com.cailanzi.mapper.OrderShopMapper;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.cailanzi.utils.PrintUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/7.
 */
@Service
@Slf4j
public class OrderServiceListener {

    @Autowired
    private OrderAsync orderAsync;
    @Autowired
    private OrderShopMapper orderShopMapper;
    @Autowired
    private OrderJdMapper orderJdMapper;

    @RabbitListener(queues = "order.create.one")
    public void orderCreateOne(MqOrder mqOrder){//京东状态码：32000
        log.info("OrderServiceListener orderCreateOne MqOrder mqOrder={}", mqOrder);
        if(orderAsync.isExitOrder(mqOrder.getBillId())){
            orderAsync.insertOrderShop(mqOrder.getBillId(),ConstantsUtil.Status.READY);
            return;
        }
        asynOrderJdByMqOrder(mqOrder);
    }

    @RabbitListener(queues = "order.delivery")
    public void deliveryToOrder(MqOrder mqOrder){//京东状态码：33040
        log.info("OrderServiceListener deliveryToOrder MqOrder mqOrder={}", mqOrder);
        if(!orderAsync.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener deliveryToOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(),ConstantsUtil.Status.DELIVERY_TO);
        }
    }

    @RabbitListener(queues = "order.finish")
    public void finishOrder(MqOrder mqOrder){//京东状态码：33060
        log.info("OrderServiceListener finishOrder MqOrder mqOrder={}", mqOrder);
        if(!orderAsync.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener finishOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(),ConstantsUtil.Status.FINISH);
        }
    }

    @RabbitListener(queues = "order.quit")
    public void quitOrder(MqOrder mqOrder){//京东状态码：20020
        log.info("OrderServiceListener quitOrder MqOrder mqOrder={}", mqOrder);
        if(!orderAsync.isExitOrder(mqOrder.getBillId())){
            log.info("OrderServiceListener quitOrder pullOrder mqOrder={}", mqOrder);
            asynOrderJdByMqOrder(mqOrder);
        }else {
            updateOrderStatus(mqOrder.getBillId(),ConstantsUtil.Status.QUIT);
        }
    }

    public String getOrderListResultData(OrderListInput orderListInput) throws Exception {
        log.info("OrderServiceListener getOrderListResultData OrderInput orderInput={}", orderListInput);
        String url = "https://openo2o.jd.com/djapi/order/es/query";
        String jdParamJson = JdHttpCilentUtil.getJdParamJson(orderListInput);
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        if(!"0".equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data.getString("result");
    }

    public void asynOrderJdByMqOrder(MqOrder mqOrder) {
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId(mqOrder.getBillId());
        orderListInput.setOrderStatus(mqOrder.getStatusId());
        try {
            String result = getOrderListResultData(orderListInput);
            JSONObject resultJson = JSON.parseObject(result);
            JSONArray jsonArray = resultJson.getJSONArray("resultList");//订单列表
            if(jsonArray!=null&&!jsonArray.isEmpty()){
                log.info("OrderServiceListener asynOrderJdByMqOrder order resultList={}",jsonArray.get(0));
                orderAsync.insertOrderJd(jsonArray);
            }
        } catch (Exception e) {
            log.error("OrderServiceListener addOrder error:",e);
        }
    }

    public void updateOrderStatus(String orderId,String status) {
        OrderListInput orderListInput = new OrderListInput();
        orderListInput.setOrderId(orderId);
        orderListInput.setOrderStatus(status);
        orderListInput.setUpdateTime(new Date());
        orderShopMapper.updateOrderStatus(orderListInput);
        orderJdMapper.updateOrderStatusByOrderId(orderListInput);
    }

    @Autowired
    private ConfigMapper configMapper;

    /*
    从创建订单中提取参数打印票据
        */
    @RabbitListener(queues = "order.create.print")
    public void orderCreateOnePrint(MqOrder mqOrder)throws Exception {
        log.info("OrderServiceListener orderCreateOnePrint MqOrder mqOrder={}", mqOrder);
        OrderListInput orderListInput = new OrderListInput();
        if(StringUtils.isEmpty(mqOrder.getBillId())){
            throw new ServiceException("获取订单号数据失败");
        }
        if(StringUtils.isEmpty(mqOrder.getStatusId())){
            throw new ServiceException("获取订单状态码数据失败");
        }
        orderListInput.setOrderId(mqOrder.getBillId());
        orderListInput.setOrderStatus(mqOrder.getStatusId());
        try {

            String url = "https://openo2o.jd.com/djapi/order/es/query";
            String jdParamJson = JdHttpCilentUtil.getJdParamJson(orderListInput);
            JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
            log.info("OrderServiceListener finishOrder pullOrder data={}", data);
            if(!"0".equals(data.getString("code"))){
                throw new ServiceException(data.getString("msg"));
            }
            String dataParam = data.getString("result");
            //resultList
            if(StringUtils.isEmpty(dataParam)){
                throw new ServiceException("获取订单列表失败");
            }
            JSONObject resultJson = JSON.parseObject(dataParam);
            //      String pageNo = resultJson.getString("pageNo");
            String dataList = resultJson.getString("resultList");
            log.info("订单列表  resultList={}", dataList);
            JSONArray list =   JSON.parseArray(dataList);
            String myData = list.get(0).toString();
            JSONObject resultList = JSON.parseObject(myData);
            String   buyerFullName =    resultList.getString("buyerFullName");//客户名
            String   produceStationName =    resultList.getString("produceStationName");//商家店名 菜蓝子-五一店

            String spaceCityStoreSn = configMapper.getValueByName(produceStationName);
            String   orderPreEndDeliveryTime =    resultList.getString("orderPreEndDeliveryTime");//预计送达结束时间

            log.info("订单列表  param={}", list.get(0));
            String   orderId = resultList.getString("orderId");//订单号
            String   buyerMobile =   resultList.getString("buyerMobile");//收货人手机号
            String   orderPayType =    resultList.getString("orderPayType");//订单支付类型（1：货到付款，4：在线支付）
            String thisOrderPayType = "";
            if(orderPayType.equals("1")){
                thisOrderPayType = "货到付款";
            }else if(orderPayType.equals("4")){
                thisOrderPayType = "在线支付";
            }
            String   buyerFullAddress =    resultList.getString("buyerFullAddress");//客户地址
            String   orderTotalMoney =    resultList.getString("orderTotalMoney");//订单商品销售价总金额
            String   orderBuyerPayableMoney =    resultList.getString("orderBuyerPayableMoney");//实际付 用户应付金额
            String   packagingMoney =    resultList.getString("packagingMoney");//包装金额
            String   orderBuyerRemark =    resultList.getString("orderBuyerRemark");//订单买家备注
            String   orderNum =    resultList.getString("orderNum");//当天门店订单序号

            //商品详情列表
            String  productData = resultList.getString("product");
            JSONArray productList =   JSON.parseArray(productData);
            log.info("商品详情列表  productList={}", productList.toString());
            //商品总数
            int products = productList.size();

            //开始构建打印文本
            StringBuffer buf = new StringBuffer();
            buf.append("#"+orderNum+"   ").append("<CB>京东到家</CB><BR>").
                    append("【立即送】"+"<BR>").
                    append(produceStationName+"<BR>").
                    append("<QR>"+ orderId +"</QR>").
                    append("预计送达："+orderPreEndDeliveryTime+"<BR>").
                    append("支付方式："+thisOrderPayType+"<BR>").
                    append("订单编号："+orderId+"<BR>").
                    append("客户姓名："+buyerFullName+"<BR>").
                    append("客户电话："+buyerMobile+"<BR>").
                    append("客户地址："+buyerFullAddress+"<BR>").
                    append("买家备注："+orderBuyerRemark+"<BR>").
                    append("--------------------------------<BR>").
                    append("品类    数量    单价    金额"+"<BR>")
//                  append( skuName   +   skuCount    +   skuJdPrice   + strCountPrices +"<BR>")
//        append( skuName   +   skuCount    +   skuJdPrice   + strCountPrices +"<BR>").

            ;

            for(int i=0;i<productList.size();i++){
                String jdOrderParam =   productList.get(i).toString();
                JSONObject productResult = JSON.parseObject(jdOrderParam);
                String skuCount =  productResult.getString("skuCount");//下单数量
                String skuName =  productResult.getString("skuName");//商品的名称
                String skuJdPrice =  productResult.getString("skuJdPrice");//到家商品销售价
                log.info("商品价格列表  skuCount={}", skuCount+"--"+skuName+"--"+skuJdPrice);

                double skuCounts =  Double.valueOf(skuCount);
                double skuJdPrices =  Double.valueOf(skuJdPrice);
                double countPrices = skuJdPrices * skuCounts/100;
                String strCountPrices  = String.valueOf(countPrices);
//            buf.append( skuName+          skuCount          + skuJdPrice+          strCountPrices+"<BR>");
                buf.append("["+skuName+"]"+          "["+skuCount+"]"          +"["+skuJdPrice+"]"+          "["+strCountPrices+"]"+"<BR>");
            }

            buf.append("--------------------------------<BR>").
                    append("总件数："+products+"<BR>").
                    append("包装费："+packagingMoney+"<BR>").
                    append("--------------------------------<BR>").
                    append("应收：   Y"+Double.valueOf(orderTotalMoney)/100+"<BR>").
                    append("实付：   Y"+Double.valueOf(orderBuyerPayableMoney)/100+"<BR>");

//            String  spaceCityStoreSn = "820501515";//*必填*：打印机编号
            String myPrintParam = PrintUtil.print(spaceCityStoreSn, buf.toString());

            log.info("打印机模板  param={}", buf.toString());
            log.info("打印机返回参数  param={}", myPrintParam.toString());

        } catch (Exception e) {
            log.error("OrderServiceListener addOrder error:",e);
        }
    }
}
