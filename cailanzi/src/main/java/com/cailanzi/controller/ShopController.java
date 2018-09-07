package com.cailanzi.controller;

import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.entities.ShopJd;
import com.cailanzi.service.ShopService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Created by v-hel27 on 2018/8/13.
 */
@Slf4j
@RestController
@RequestMapping("shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @RequestMapping("syncShop")
    public SysResult syncShop() throws Exception {
        shopService.syncShop();
        return SysResult.build(200);
    }

    @RequestMapping("list")
    public EasyUIResult pageList(Integer page, Integer rows) throws Exception {
        return shopService.pageList(page,rows);
    }

    @RequestMapping(value = "compgirdList",method = RequestMethod.POST)
    public List<ShopJd> compgirdList(String q) throws Exception {
        return shopService.compgirdList(q);
    }

}
