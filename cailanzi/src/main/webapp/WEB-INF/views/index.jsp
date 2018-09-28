<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>菜篮子后台管理系统</title>
<jsp:include page="/commons/common-js.jsp"></jsp:include>
<style type="text/css">
	.content {
		padding: 10px 10px 10px 10px;
	}
</style>
</head>
<body class="easyui-layout">
    <div data-options="region:'west',title:'菜单',split:true" style="width:220px;">
    	<ul id="menu" class="easyui-tree" style="margin-top: 10px;margin-left: 5px;">
         	<li>
         		<span>商品管理</span>
         		<ul>
					<li data-options="attributes:{'url':'/page/item-add'}">新增商品-商户</li>
					<li data-options="attributes:{'url':'/page/item-list'}">查询商品-商户</li>
					<li data-options="attributes:{'url':'/page/item-jd-list'}">查询商品-京东</li>
					<li data-options="attributes:{'url':'/page/item-status-list'}">查询商品-京东库存</li>
					<li data-options="attributes:{'url':'/page/item-category-list'}">查询商品类目</li>
					<li data-options="attributes:{'url':'/page/item-img-list'}">查询商品图片</li>
					<li data-options="attributes:{'url':'/page/item-img-category-list'}">查询商品图片类目</li>
	         	</ul>
         	</li>
         	<li>
         		<span>订单管理</span>
         		<ul>
	         		<li data-options="attributes:{'url':'/page/order'}">查询订单</li>
	         		<li data-options="attributes:{'url':'/page/order-shop'}">查询订单-商户</li>
	         	</ul>
         	</li>
         	<li>
         		<span>商户管理</span>
         		<ul>
	         		<li data-options="attributes:{'url':'/page/shop'}">查询门店</li>
	         		<li data-options="attributes:{'url':'/page/user'}">查询用户</li>
	         	</ul>
         	</li>
         	<li>
         		<span>规则管理</span>
         		<ul>
	         		<li data-options="attributes:{'url':'/page/rule'}">查询规则</li>
	         		<li data-options="attributes:{'url':'/page/rule-item'}">查询规则-商品</li>
	         	</ul>
         	</li>
         </ul>
    </div>
    <div data-options="region:'center',title:''">
    	<div id="tabs" class="easyui-tabs">
		    <div title="首页" style="padding:20px;">
		        	
		    </div>
		</div>
    </div>
    
<script type="text/javascript">
$(function(){
	$('#menu').tree({
		onClick: function(node){
			if($('#menu').tree("isLeaf",node.target)){
				var tabs = $("#tabs");
				var tab = tabs.tabs("getTab",node.text);
				if(tab){
					tabs.tabs("select",node.text);
				}else{
					tabs.tabs('add',{
					    title:node.text,
					    href: node.attributes.url,
					    closable:true,
					    bodyCls:"content"
					});
				}
			}
		}
	});
});
</script>
</body>
</html>