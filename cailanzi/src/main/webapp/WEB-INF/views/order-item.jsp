<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    订单ID:&nbsp;&nbsp;<input  class="easyui-textbox" id="orderItemJdList_searchOrderId">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="orderItemJdListSearch()">搜索</button>
</div>
<table id="order-item-jd-list" style="width:100%;height:700px"></table>

<script type="text/javascript">
    $('#order-item-jd-list').datagrid({
        url:'/order/orderProductList',
        title: '订单商品列表',
        pagePosition: 'top',
        singleSelect: true ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        columns:[[
            {field:'id',checkbox:true},
            {field:'orderId',title:'订单ID',width:130,align:'center'},
            {field:'skuId',title:'到家商品编码',width:100,align:'center'},
            {field:'skuName',title:'商品名称',width:300,align:'center'},
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
            {field:'skuCount',title:'下单数量',width:70,align:'center'},
            {field:'skuStorePrice',title:'到家商品门店价（分）',width:150,align:'center'},
            {field:'skuJdPrice',title:'到家商品销售价（分）',width:150,align:'center'},
            {field:'skuCostPrice',title:'到家商品成本价（分）',width:150,align:'center'},
            {field:'skuWeight',title:'商品重量（千克）',width:120,align:'center'},
            {field:'promotionType',title:'促销类型',width:70,align:'center',
                formatter: function(value,row,index){
                    if(value==1){
                        return "无优惠";
                    }else if(value==3){
                        return "单品直降";
                    }else if(value==4){
                        return "限时抢购";
                    }
                    return value;
                }
            }
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.orderId = $('#orderItemJdList_searchOrderId').val();
            return true;
        }
    });
    orderItemJdListSearch = function () {
        $("#order-item-jd-list").datagrid('load');
    }

</script>