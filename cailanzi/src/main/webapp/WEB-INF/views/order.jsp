<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            {field:'orderId',title:'订单ID',width:120,align:'center'},
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
            {field:'status',title:'订单状态',width:60,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},

            {field:'skuId',title:'到家商品编码',width:100,align:'center'},
            {field:'skuName',title:'商品名称',width:200,align:'center'},
            {field:'skuCount',title:'下单数量',width:60,align:'center'},
            {field:'skuStorePrice',title:'到家商品门店价（分）',width:150,align:'center'},
            {field:'skuJdPrice',title:'到家商品销售价（分）',width:150,align:'center'},
            {field:'skuCostPrice',title:'到家商品成本价（分）',width:150,align:'center'},
            {field:'skuWeight',title:'商品重量（千克）',width:120,align:'center'}
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            return true;
        }
    });
</script>