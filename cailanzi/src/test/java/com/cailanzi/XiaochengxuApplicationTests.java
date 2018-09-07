package com.cailanzi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cailanzi.mapper.OrderJdMapper;
import com.cailanzi.mapper.ProductJdMapper;
import com.cailanzi.mapper.ProductMapper;
import com.cailanzi.pojo.OrderListInput;
import com.cailanzi.pojo.entities.ProductJd;
import com.cailanzi.service.OrderService;
import com.cailanzi.service.ProductService;
import com.cailanzi.service.ShopService;
import com.cailanzi.utils.ConstantsUtil;
import com.cailanzi.utils.HttpClientUtil;
import com.cailanzi.utils.JdHttpCilentUtil;
import com.cailanzi.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XiaochengxuApplicationTests {

	@Autowired
	private ProductJdMapper productJdMapper;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ProductService productService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderJdMapper orderJdMapper;

	@Test
	public void testDeliveryOrderIdsOfOrderJd() throws Exception {
		Set<String> orderIds = new HashSet<>();
		orderIds.add("821342469000021");
		orderIds.add("821325390000141");
		System.out.println(orderJdMapper.getDeliveryOrderIdsOfOrderJd(orderIds, ConstantsUtil.Status.READY));
	}

	@Test
	public void testGetOrderListResultData() throws Exception {
		OrderListInput orderListInput = new OrderListInput();
		orderListInput.setBelongStationNo("11673747");//菜蓝子－扫把塘店
		System.out.println(orderService.getOrderListResultData(orderListInput));
	}

	@Test
	public void testGetCategoriesBasic() throws Exception {
		System.out.println(productService.getCategoriesBasic());
	}

	@Test
	public void testAsynProductStatus() throws Exception {
//		productService.asynProductStatus();
	}

	@Test
	public void testGetShopDetail() throws Exception {
		System.out.println(shopService.getShopDetail("11673746"));
	}

	@Test
	public void testGetShopCodes() throws Exception {
		System.out.println(shopService.getShopCodes());
		//["11673746","11673747","11673749","11673751","11673757","11673780","11680242","11681522","11681523","11681524","11685041","11685045","11685205","11685206","11685208","11715274","11715275","11715276","11715278","11715279","11715281","11715282","11715283","11737728","11771654","11771655","11771656","11771657","11771658","11797977","11797980","11797981","11797983","11797985","11797986"]
	}

	@Test
	public void commonMapper() {
		ProductJd jd = new ProductJd();
		jd.setId(1);
		ProductJd j = productJdMapper.selectOne(jd);
		System.out.println(j);
	}


	@Test
	public void contextLoads() {
		String password = "123456";
		System.out.println(MD5Util.getMD5String(password));
		//e10adc3949ba59abbe56e057f20f883e

		String ss = "adminadmin";
		System.out.println(MD5Util.getMD5String(ss));
	}

	@Test
	public void testHttpClient() throws UnsupportedEncodingException {
		String url = "https://openo2o.jd.com/djapi/order/es/query";
		Map<String,Object> map = new HashMap<>();
		map.put("v","1.0");
		map.put("format","json");
		map.put("app_key","6bd9123fd3224c4299e06c9a9651a5cf");
		map.put("app_secret","810f1b6b35fa4d9d8898c551387f353e");
		map.put("token","8bf2ba29-573a-434c-896c-4e2926926925");
		map.put("jd_param_json","{\"businessType_list\":[]}");
		map.put("sign","6668EAFBD9A1BEB46C61A9ED66A5C5EA");
		map.put("timestamp","2018-08-03 17:26:35");

		String s = HttpClientUtil.doGet(url,map);
		System.out.println(s);
	}

	@Test
	public void testJdHttpCilentUtil() throws Exception {
		String url = "https://openo2o.jd.com/djapi/order/es/query";
		String jd_param_json = "{\"orderNo\":\"1\"}";
		JSONObject data = JdHttpCilentUtil.doGetAndGetData(url, jd_param_json);
		System.out.println(data);
	}

	private static void get(int sum,int curr,int remainBottle,int remainCap){
		int temp = curr;
		curr = (temp+remainBottle)/2 + (temp+remainCap)/3;
		if(curr != 0){
			sum += curr;
			remainBottle = (temp+remainBottle)%2;
			remainCap = (temp+remainCap)%3;
			get(sum,curr,remainBottle,remainCap);
		}else{
			System.out.println(sum);
		}
	}

	@Test
	public void test(){
		int sum = 20;
		int curr = 20;
		int remainBottle = 0;
		int remainCap = 0;
		get(sum,curr,remainBottle,remainCap);
	}

	private static int get1(int curr,int remainBottle,int remainCap){
		int sum = curr;
		if(curr != 0){
			int temp = curr;
			curr = (temp+remainBottle)/2 + (temp+remainCap)/3;
			remainBottle = (temp+remainBottle)%2;
			remainCap = (temp+remainCap)%3;
			sum += get1(curr,remainBottle,remainCap);
			return sum;
		}else{
			return 0;
		}
	}

	@Test
	public void test1(){
		int curr = 20;
		int remainBottle = 0;
		int remainCap = 0;
		System.out.println(get1(curr, remainBottle, remainCap));
	}




}
