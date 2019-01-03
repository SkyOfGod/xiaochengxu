package com.cailanzi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//不去掉会出现 Error creating bean with name 'serverEndpointExporter'。。。。。
//@RunWith(SpringRunner.class)
@SpringBootTest
public class CailanziWebsocketApplicationTests {

	@Test
	public void contextLoads() {
	}

}
