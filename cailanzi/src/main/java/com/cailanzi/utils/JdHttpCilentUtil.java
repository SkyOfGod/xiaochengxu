package com.cailanzi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.Exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    private static String appKey = "6bd9123fd3224c4299e06c9a9651a5cf";

    private static String appSecret = "810f1b6b35fa4d9d8898c551387f353e";

    private static String token = "8bf2ba29-573a-434c-896c-4e2926926925";

    private final static String V = "1.0";

    private final static String FORMAT = "json";

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
     * @return
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
        param.put("app_key",appKey);
        param.put("token",token);
        param.put("jd_param_json",jdParamJson);
        param.put("timestamp",timestamp);
        String sign = JdHelper.getSign(param,appSecret);

        Map<String,Object> map = new HashMap<>();
        map.put("v",V);
        map.put("format",FORMAT);
        map.put("app_key",appKey);
        map.put("app_secret",appSecret);
        map.put("token",token);
        map.put("jd_param_json",jdParamJson);
        map.put("timestamp",timestamp);
        map.put("sign",sign);

        log.info("JdHttpCilentUtil doGet map={}", map);
        return HttpClientUtil.doGet(url,map);
    }

    /**
     * 将对象转为json对象格式
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
