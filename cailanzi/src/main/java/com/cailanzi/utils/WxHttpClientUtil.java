package com.cailanzi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.ConfigMapper;
import com.cailanzi.pojo.entities.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/10/5.
 */
@Slf4j
public class WxHttpClientUtil {

    public static String APP_SECRET ;

    public static String APP_ID ;

    public static String NEW_ORDER_TEMPLATE_ID ;

    private final static Integer EXPIRE_TIME = 7200;//access_token到期时间为2小时

    static {
        ConfigMapper configMapper = SpringContextUtil.getBean(ConfigMapper.class);
        APP_SECRET = configMapper.getValueByName("app_secret_wx");
        APP_ID = configMapper.getValueByName("app_id_wx");
        NEW_ORDER_TEMPLATE_ID = configMapper.getValueByName("new_order_template_id");
    }

    public static JSONObject getOpenIdAndSessionKey(String code) throws ServiceException, UnsupportedEncodingException {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String,Object> map = new HashMap<>();
        map.put("appid",APP_ID);
        map.put("secret", APP_SECRET);
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String result = HttpClientUtil.doGet(url,map);
        log.info("WxHttpClientUtil getOpenIdAndSessionKey result={}",result);
        JSONObject jsonObject = JSONObject.parseObject(result);

        String errcode = jsonObject.getString("errcode");
        if(StringUtils.isNotBlank(errcode)){
            String msg = "";
            if("-1".equals(errcode)){
                msg = "系统繁忙，此时请开发者稍候再试";
            }else if("40029".equals(errcode)||"41008".equals(errcode)){
                msg = "code 无效";
            }else if("45011".equals(errcode)){
                msg = "频率限制，每个用户每分钟100次";
            }else{
                msg = jsonObject.getString("errmsg");
            }
            throw new ServiceException(msg);
        }
        return jsonObject;
    }

    public static void sendTemplateMsg(String openId,JSONObject data) throws UnsupportedEncodingException {
        ConfigMapper configMapper = SpringContextUtil.getBean(ConfigMapper.class);
        String formId = configMapper.getOneFormId();
        if(StringUtils.isBlank(formId)){
            return;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="+getAccessToken();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser",openId);
        jsonObject.put("template_id",NEW_ORDER_TEMPLATE_ID);
//        jsonObject.put("page","/pages/order/order");
        jsonObject.put("form_id",formId);
        jsonObject.put("data",data);
        log.info("WxHttpClientUtil sendTemplateMsg jsonObject={}",jsonObject);
        String result = HttpClientUtil.doPost(url,jsonObject);
        log.info("WxHttpClientUtil sendTemplateMsg result={}",result);
        JSONObject resultJson = JSON.parseObject(result);
        if("0".equals(resultJson.getString("errcode"))||"ok".equals(resultJson.getString("errmsg"))){
            configMapper.updateValidByFormId(formId);
        }
        if("41028".equals(resultJson.getString("errcode"))){//invalid form id hint: [qIVzOa06104924]
            log.info("WxHttpClientUtil sendTemplateMsg invalid formId={}",formId);
            configMapper.updateValidByFormId(formId);
            sendTemplateMsg(openId,data);
        }
    }

    public static String getAccessToken(){
        ConfigMapper configMapper = SpringContextUtil.getBean(ConfigMapper.class);
        String accessToken ;
        Config config = new Config();
        config.setName("access_token_wx");
        Config selConfig = configMapper.selectOne(config);
        long diff = selConfig.getCreateTime().getTime() + (EXPIRE_TIME-5*60)*1000 - System.currentTimeMillis();
        if(diff >= 0){//有效时间内
            accessToken = selConfig.getValue();
        }else{
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APP_ID+"&secret="+APP_SECRET;
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSONObject.parseObject(result);
            accessToken = jsonObject.getString("access_token");
            if(StringUtils.isNotBlank(accessToken)){
                configMapper.updateValueByName("access_token_wx",accessToken,new Date());
            }
        }
        return accessToken;
    }

}
