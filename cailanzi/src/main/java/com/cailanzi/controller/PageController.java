package com.cailanzi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("page")
public class PageController {

	@RequestMapping("/{pageName}")
	public String goHome(@PathVariable String pageName){
		return pageName;
	}

}
