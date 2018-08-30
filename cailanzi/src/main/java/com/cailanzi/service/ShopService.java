package com.cailanzi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.ShopMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.pojo.entities.ShopJd;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/8/13.
 */
@Slf4j
@Service
public class ShopService {

    private static final int SHOP_ID = 310143;

    @Autowired
    private ShopMapper shopMapper;

    public void syncShop() throws Exception {
        Date date = new Date();

        JSONArray codes = getShopCodes();
        List<ShopJd> list = new ArrayList<>();
        for (Object code : codes) {
            JSONObject jsonObject = JSONObject.parseObject(getShopDetail(code.toString()));
            String createTm = jsonObject.getString("createTime");
            jsonObject.remove("createTime");
            String updateTm = jsonObject.getString("updateTime");
            jsonObject.remove("updateTime");

            ShopJd jd = JSONObject.toJavaObject(jsonObject,ShopJd.class);
            jd.setCreateTime(toDate(createTm));
            jd.setUpdateTime(toDate(updateTm));
            jd.setSyncTime(date);
            list.add(jd);
        }
        if(!list.isEmpty()){
            shopMapper.truncateShopJd();
            shopMapper.insertList(list);
        }
    }

    private Date toDate(String str){
        JSONObject jsonObject = JSONObject.parseObject(str);
        String time = jsonObject.getString("time");
        return new Date(Long.parseLong(time));
    }

    public JSONArray getShopCodes() throws Exception {
        String url = "https://openo2o.jd.com/djapi/store/getStationsByVenderId";
        String jdParamJson = "{}";
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson,"1");
        JSONArray jsonArray = data.getJSONArray("result");
        log.info("ShopService getShopCodes return jsonArray={}", jsonArray);
        return jsonArray;
    }

    public String getShopDetail(String storeNo) throws Exception {
        String url = "https://openo2o.jd.com/djapi/storeapi/getStoreInfoByStationNo";
        String jdParamJson = "{StoreNo:'"+storeNo+"'}";
        JSONObject data = JdHttpCilentUtil.doGetAndGetData(url,jdParamJson);
        String detail = data.getString("result");
        log.info("ShopService getShopDetail return detail={}", detail);
        return detail;
    }

    public EasyUIResult pageList(Integer page,Integer rows) {
        PageHelper.startPage(page,rows);
        List<ShopJd> list = shopMapper.selectAll();
        log.info("ShopService pageList list={}", list);
        PageInfo<ShopJd> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public List<ShopJd> compgirdList(String q) {
        log.info("ShopService compgirdList q={}", q);
        List<ShopJd> list = shopMapper.getCompgirdList(q);
        log.info("ShopService compgirdList return list={}", list);
        return list;
    }
}
