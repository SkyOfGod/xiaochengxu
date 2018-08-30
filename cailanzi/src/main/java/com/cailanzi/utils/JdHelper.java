package com.cailanzi.utils;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by v-hel27 on 2018/8/7.
 */
public class JdHelper {

    private static String concatParams(Map<String, String> params2) {
        Object[] key_arr = params2.keySet().toArray();
        Arrays.sort(key_arr);
        StringBuilder str = new StringBuilder();
        for (Object key : key_arr) {
            String val = params2.get(key);
            str.append(key).append(val);
        }
        return str.toString();
    }

    /**
     * 1.将token,app_key,timestamp,format,v,jd_param_json{} 排序为app_key,format,jd_param_json{},timestamp,token,v
     * 2.把所有参数名和参数值进行拼装: app_keyxxxformatxxxxjd_param_json{xxxx}timestampxxxxxxtokenxxxvx
     * 3.把appSecret夹在字符串的两端: appSecret+XXXX+appSecret
     * @param param
     * @param appSecret
     * @return
     */
    public static String getSign(Map<String,String> param,String appSecret ) throws Exception{
        String sysStr=concatParams(param);
        StringBuilder resultStr=new StringBuilder();
        resultStr.append(appSecret).append(sysStr).append(appSecret);
        return MD5Util.getMD5String(resultStr.toString()).toUpperCase();
    }
}
