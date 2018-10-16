package com.cailanzi.service;

import com.alibaba.fastjson.JSONObject;
import com.cailanzi.exception.ServiceException;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.mapper.UserMapper;
import com.cailanzi.pojo.EasyUIResult;
import com.cailanzi.pojo.SysResult;
import com.cailanzi.pojo.UserImport;
import com.cailanzi.pojo.entities.Product;
import com.cailanzi.pojo.entities.User;
import com.cailanzi.utils.MD5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by v-hel27 on 2018/8/11.
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;

    public SysResult login(String username, String password, Integer type) throws ServiceException{
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            throw new ServiceException("数据不能为空");
        }
        List<User> userList = userMapper.selectByUsername(username,type);
        if(userList.isEmpty()){
            throw new ServiceException("用户名不存在");
        }
        String passwordMd5 = MD5Util.getMD5String(password);
        if(!userList.get(0).getPassword().equals(passwordMd5)){
            throw new ServiceException("密码不正确");
        }
        return SysResult.ok(userList.get(0));
    }

    public EasyUIResult userList(UserImport userImport) {
        PageHelper.startPage(userImport.getPage(),userImport.getRows());

        User user = new User();
        if(StringUtils.isNotBlank(userImport.getBelongStationNo())){
            user.setBelongStationNo(userImport.getBelongStationNo());
        }
        List<User> list = userMapper.select(user);
        log.info("UserService userList list={}", list);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    public void addUser(User user) throws ServiceException{
        User tempUser = new User();
        tempUser.setUsername(user.getUsername());
        List<User> tempList = userMapper.select(tempUser);
        if(!tempList.isEmpty()){
            throw new ServiceException("用户名已经存在！");
        }

        user.setCreateTime(new Date());
        String passwordMd5 = MD5Util.getMD5String(user.getPassword());
        user.setPassword(passwordMd5);
        String signMD5 = MD5Util.getMD5String(user.getUsername()+user.getPassword()+user.getType()+user.getBelongStationNo());
        user.setSign(signMD5);
        log.info("UserService addUser user={}", user);
        userMapper.insertSelective(user);
    }

    public void editUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    public void deleteUser(String names) {
        log.info("UserService deleteUser ids={}", names);
        String[] arr = names.split(",");
        for (String name : arr) {
            User user = new User();
            user.setUsername(name);
            userMapper.delete(user);

            Product product = new Product();
            product.setPhone(name);
            productMapper.delete(product);
        }
    }

    public List<User> comgridList(String q) {
        log.info("UserService comgridList q={}", q);
        List<User> list = userMapper.comgridList(q);
        log.info("UserService comgridList return list={}", list);
        return list;
    }


}
