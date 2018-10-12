package com.cailanzi.service;

import com.cailanzi.mapper.ConfigMapper;
import com.cailanzi.pojo.JdResult;
import com.cailanzi.pojo.entities.Config;
import com.cailanzi.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/10.
 */
@Service
@Slf4j
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    public JdResult saveToken(HttpServletRequest request) {
        Config config = new Config();
        config.setName("token_jd");
        config.setValue(request.getParameter("token"));
        config.setCreateTime(new Date());
        log.info("ConfigService saveToken Config config={}", config);
        configMapper.insert(config);
        return JdResult.ok(config);
    }

}
