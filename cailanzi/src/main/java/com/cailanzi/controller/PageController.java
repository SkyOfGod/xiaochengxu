package com.cailanzi.controller;

import com.cailanzi.pojo.SysResult;
import com.cailanzi.service.UserService;
import com.cailanzi.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("page")
public class PageController {

	@Autowired
	private UserService userService;

	@RequestMapping("/{pageName}")
	public String goHome(@PathVariable String pageName, HttpSession session){
		Object sign = session.getAttribute("sign");
		if(sign==null){
			return "login";
		}
		SysResult sysResult = userService.isExitSign(sign.toString());
		if(sysResult.getStatus() != 200){
			return "login";
		}
		return pageName;
	}

}
