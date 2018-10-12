package com.cailanzi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Exception.ServiceException;
import com.cailanzi.mapper.ConfigMapper;
import com.cailanzi.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/8/7.
 */
@Slf4j
public class JdHttpCilentUtil {

    @Autowired
    private ConfigService configService;

    public final static String V = "1.0";

    public final static String FORMAT = "json";

    public static String APP_KEY ;

    public static String APP_SECRET ;

    public static String TAKEN_JD ;

    static {
        ConfigMapper configMapper = SpringContextUtil.getBean(ConfigMapper.class);
        APP_KEY = configMapper.getValueByName("app_key_jd");
        APP_SECRET = configMapper.getValueByName("app_secret_jd");
        TAKEN_JD = configMapper.getValueByName("token_jd");
    }

    public static JSONObject doGetAndGetData(String url,String jdParamJson,String successCode,String successCodeKey,String msgKey) throws Exception {
        JSONObject data = doGetAndGetDataBasic(url,jdParamJson);
        if(!successCode.equals(data.getString(successCodeKey))){
            throw new ServiceException(data.getString(msgKey));
        }
        return data;
    }

    /**
     * ,msgKey="msg"
     * @param url
     * @param jdParamJson
     * @param successCode
     * @param successCodeKey
     * @return
     * @throws Exception
     */
    public static JSONObject doGetAndGetData(String url,String jdParamJson,String successCode,String successCodeKey) throws Exception {
        JSONObject data = doGetAndGetDataBasic(url,jdParamJson);
        if(!successCode.equals(data.getString(successCodeKey))){
            throw new ServiceException(data.getString("msg"));
        }
        return data;
    }

    /**
     * successCodeKey="code",msgKey="msg"
     * @param url
     * @param jdParamJson
     * @param successCode
     * @return
     * @throws Exception
     */
    public static JSONObject doGetAndGetData(String url,String jdParamJson,String successCode) throws Exception {
        JSONObject data = doGetAndGetDataBasic(url,jdParamJson);
        if(!successCode.equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data;
    }

    /**
     * successCode="0",successCodeKey="code",msgKey="msg"
     * @param url
     * @param jdParamJson
     * @return result的data数据（效验过两层的“code”是否等于0）
     * @throws Exception
     */
    public static JSONObject doGetAndGetData(String url,String jdParamJson) throws Exception {
        JSONObject data = doGetAndGetDataBasic(url,jdParamJson);
        if(!"0".equals(data.getString("code"))){
            throw new ServiceException(data.getString("msg"));
        }
        return data;
    }

    /**
     * @param url
     * @param jdParamJson
     * @return
     * @throws Exception
     */
    private static JSONObject doGetAndGetDataBasic(String url,String jdParamJson) throws Exception {
        JSONObject result = JSONObject.parseObject(doGet(url,jdParamJson));
        log.info("JdHttpCilentUtil doGet result={}", result);
        if(!"0".equals(result.getString("code"))){
            throw new ServiceException(result.getString("msg"));
        }
        JSONObject data = JSONObject.parseObject(result.getString("data"));
        log.info("JdHttpCilentUtil doGetAndGetData data={}", data);
        return data;
    }

    /**
     * jdParamJson是JSON格式字符串：{}
     * @param url
     * @param jdParamJson
     * @return
     * @throws Exception
     */
    public static String doGet(String url,String jdParamJson) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter).toString();

        Map<String,String> param = new HashMap<>();
        param.put("v",V);
        param.put("format",FORMAT);
        param.put("app_key",APP_KEY);
        param.put("token",TAKEN_JD);
        param.put("jd_param_json",jdParamJson);
        param.put("timestamp",timestamp);
        String sign = JdHelper.getSign(param,APP_SECRET);

        Map<String,Object> map = new HashMap<>();
        map.put("v",V);
        map.put("format",FORMAT);
        map.put("app_key",APP_KEY);
        map.put("app_secret",APP_SECRET);
        map.put("token",TAKEN_JD);
        map.put("jd_param_json",jdParamJson);
        map.put("timestamp",timestamp);
        map.put("sign",sign);

        log.info("JdHttpCilentUtil doGet map={}", map);
        return HttpClientUtil.doGet(url,map);
    }

    /**
     * 将java实体对象转为json对象格式
     * @param t
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> String getJdParamJson(T t) throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        Class clz = t.getClass();
        for (Field field : clz.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.get(t)!=null&& StringUtils.isNotBlank(field.get(t).toString())){
                map.put(field.getName(),field.get(t));
            }
        }
        log.info("JdHttpCilentUtil getJdParamJson return map={}", map);
        return JSON.toJSONString(map);
    }


}
