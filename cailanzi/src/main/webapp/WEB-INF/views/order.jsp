<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    订单ID:&nbsp;&nbsp;<input  class="easyui-textbox" id="orderJdList_searchOrderId">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="orderJdListSearch()">搜索</button>
</div>
<table id="order-jd-list" style="width:100%;height:800px"></table>

<script type="text/javascript">
    $('#order-jd-list').datagrid({
        url:'/order/orderList',
        title: '订单列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        columns:[[
            {field:'id',checkbox:true},
            {field:'orderId',title:'订单ID',width:130,align:'center'},
            {field:'orderNum',title:'订单编号',width:60,align:'center',
                formatter: function(value,row,index){
                    return "#"+value;
                }
            },
            {field:'produceStationNo',title:'所属门店编码',width:100,align:'center'},
            {field:'produceStationName',title:'所属门店名称',width:150,align:'center'},
            {field:'buyerFullName',title:'购买人',width:60,align:'center'},
            {field:'buyerFullAddress',title:'购买人地址',width:100,align:'center'},
            {field:'buyerMobile',title:'购买人电话',width:100,align:'center'},
            {field:'status',title:'订单状态',width:70,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},

            {field:'skuId',title:'到家商品编码',width:100,align:'center'},
            {field:'skuName',title:'商品名称',width:200,align:'center'},
            {field:'skuStatus',title:'商品状态',width:70,align:'center',
                formatter: function(value,row,index){
                    if(value==0){
                        return "待拣货";
                    }else if(value==1){
                        return '<span style="color:red;">已拣货</span>';
                    }
                    return value;
                }
            },
            {field:'skuCount',title:'下单数量',width:60,align:'center'},
            {field:'skuStorePrice',title:'到家商品门店价（分）',width:150,align:'center'},
            {field:'skuJdPrice',title:'到家商品销售价（分）',width:150,align:'center'},
            {field:'skuCostPrice',title:'到家商品成本价（分）',width:150,align:'center'},
            {field:'skuWeight',title:'商品重量（千克）',width:120,align:'center'}
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.orderId = $('#orderJdList_searchOrderId').val();
            return true;
        }
    });
    orderJdListSearch = function () {
        $('#order-jd-list').datagrid('load');
    }
</script>