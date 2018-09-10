package com.cailanzi.service;

import com.cailanzi.mapper.ConfigMapper;
import com.cailanzi.pojo.JdResult;
import com.cailanzi.pojo.entities.TokenJd;
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
        TokenJd tokenJd = new TokenJd();
        tokenJd.setToken(request.getParameter("token"));
        tokenJd.setExpiresIn(request.getParameter("expires_in"));
        tokenJd.setTime(request.getParameter("time"));
        tokenJd.setUid(request.getParameter("uid"));
        tokenJd.setUserNick(request.getParameter("user_nick"));
        tokenJd.setVenderId(request.getParameter("venderId"));
        log.info("ConfigService saveToken TokenJd tokenJd={}", tokenJd);
        tokenJd.setCreateTime(new Date());
        configMapper.insert(tokenJd);
        return JdResult.ok(tokenJd);
    }

}
