package com.cailanzi.controller;

import com.cailanzi.Exception.ServiceException;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.UserImport;
import com.cailanzi.pojo.entities.User;
import com.cailanzi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by v-hel27 on 2018/8/11.
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /****************** 小程序管理 *********************/

    /**
     * 小程序登录验证 并获取保存openId
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "web/login",method = RequestMethod.GET)
    public SysResult login(String username, String password, String code) throws UnsupportedEncodingException {
        log.info("UserController login username={}",username);
        SysResult sysResult;
        try {
            sysResult = userService.loginAndSaveCode(username,password,code);
        }catch (ServiceException e){
            return SysResult.build(400,e.getMessage());
        }
        return sysResult;
    }

    /**
     * 小程序登录验证
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "web/isExitSign",method = RequestMethod.GET)
    public SysResult isExitSign(String sign) {
        log.info("UserController isExitSign sign={}",sign);
        if(StringUtils.isBlank(sign)){
            return SysResult.build(400);
        }
        return userService.isExitSign(sign);
    }

    /****************** 后台管理 *********************/

    /**
     * 后台登录验证
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "manageLogin",method = RequestMethod.POST)
    public SysResult manageLogin(String username, String password, HttpSession session){
        log.info("UserController manageLogin username={}",username);
        SysResult sysResult;
        try {
            sysResult = userService.login(username,password,0);
        }catch (ServiceException e){
            return SysResult.build(400,e.getMessage());
        }
        if(sysResult.getData()!=null){
            User user = (User) sysResult.getData();
            session.setAttribute("sign",user.getSign());
        }
        return sysResult;
    }

    @RequestMapping("userPage")
    public EasyUIResult userList(UserImport userImport){
        log.info("UserController userList start");
        EasyUIResult sysResult = userService.userList(userImport);
        log.info("UserController userList return {}", sysResult);
        return sysResult;
    }

    @RequestMapping("addUser")
    public SysResult addUser(User user){
        log.info("UserController addUser user = {}",user);
        try {
            userService.addUser(user);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.build(200);
    }

    @RequestMapping("editUser")
    public SysResult editUser(User user){
        log.info("UserController editUser user = {}",user);
        try {
            userService.editUser(user);
        }catch (ServiceException e){
            return SysResult.build(201,e.getMessage());
        }
        return SysResult.build(200);
    }

    @RequestMapping("deleteUser")
    public SysResult deleteUser(String names){
        log.info("UserController deleteUser String names={}",names);
        userService.deleteUser(names);
        return SysResult.build(200);
    }

    @RequestMapping(value = "comgridList",method = RequestMethod.POST)
    public List<User> comgridList(String q) throws Exception {
        return userService.comgridList(q);
    }
}
