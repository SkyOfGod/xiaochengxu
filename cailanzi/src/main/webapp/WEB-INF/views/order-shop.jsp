<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    商户账号:&nbsp;&nbsp;<input  class="easyui-textbox" id="orderShopList_searchUsername">
    订单ID:&nbsp;&nbsp;<input  class="easyui-textbox" id="orderShopList_searchOrderId">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="orderShopListSearch()">搜索</button>
</div>
<table id="order-shop-list" style="width:100%;height:700px"></table>

<script type="text/javascript">
    $('#order-shop-list').datagrid({
        url:'/order/orderShopList',
        title: '商家订单列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        columns:[[
            {field:'id',checkbox:true},
            {field:'username',title:'商户账号',width:120,align:'center'},
            {field:'belongStationNo',title:'所属门店编码',width:100,align:'center'},
            {field:'orderId',title:'订单ID',width:150,align:'center'},
            {field:'orderStatus',title:'订单状态',width:70,align:'center',
                formatter: function(value,row,index){
                    if(value=="32000"){
                        return "待发货";
                    }else if(value=="33000"){
                        return '<span style="color:red;">待配送</span>';
                    }else if(value=="34000"){
                        return "配送中";
                    }else if(value=="35000"){
                        return "已完成";
                    }else if(value=="36000"){
                        return "已取消";
                    }
                    return value;
                }
            },
            {field:'skuId',title:'到家商品编码',width:120,align:'center'},
            {field:'skuName',title:'商品名称',width:200,align:'center'},
            {field:'skuCount',title:'下单数量',width:70,align:'center'},
            {field:'skuPrice',title:'商品价格（分）',width:100,align:'center'},
            {field:'skuStatus',title:'商品状态',width:70,align:'center',
                formatter: function(value,row,index){
                    if(value==0){
                        return "待拣货";
                    }else if(value==1){
                        return '<span style="color:blue;">已拣货</span>';
                    }else if(value==2){
                        return '<span style="color:red;">缺货</span>';
                    }
                    return value;
                }
            },
            {field:'createTime',title:'创建时间',width:150,align:'center'},
            {field:'updateTime',title:'跟新时间',width:150,align:'center'}
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.username = $('#orderShopList_searchUsername').val();
            param.orderId = $('#orderShopList_searchOrderId').val();
            return true;
        }
    });
    orderShopListSearch = function () {
        $('#order-shop-list').datagrid('load');
    }
</script>