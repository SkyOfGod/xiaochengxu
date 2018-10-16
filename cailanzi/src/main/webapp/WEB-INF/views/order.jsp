<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    订单ID:&nbsp;&nbsp;<input  class="easyui-textbox" id="orderJdList_searchOrderId">
    所属门店编码:&nbsp;&nbsp;<input  class="easyui-textbox" id="orderJdList_searchStationNo">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="orderJdListSearch()">搜索</button>
</div>
<table id="order-jd-list" style="width:100%;height:800px"></table>

<script type="text/javascript">
    $('#order-jd-list').datagrid({
        url:'/order/orderList',
        title: '订单列表',
        pagePosition: 'top',
        singleSelect: true ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [
            {
                text : '删除',
                iconCls : 'icon-cancel',
                handler : function() {deleteOrder();}
            },
        ],
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
            {field:'status',title:'订单状态',width:70,align:'center',
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
            {field:'createTime',title:'创建时间',width:150,align:'center'},
            {field:'updateTime',title:'修改时间',width:150,align:'center'},
            {field:'orderStartTime',title:'订单开始时间',width:150,align:'center'},
            {field:'orderBuyerRemark',title:'备注',width:600,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.orderId = $('#orderJdList_searchOrderId').val();
            param.deliveryStationNo = $('#orderJdList_searchStationNo').val();
            return true;
        }
    });
    orderJdListSearch = function () {
        $("#order-jd-list").datagrid('load');
    }
    deleteOrder = function () {
        var order = $("#order-jd-list").datagrid("getSelected");
        if(!order){
            $.messager.alert('提示', '未选中商品!');
            return;
        }
        $.messager.confirm('确认', '确定删除订单号为 ' + order.orderId + ' 的数据吗？', function(r) {
            if (r) {
                var params = {"orderId" : order.orderId};
                $.post("/order/deleteOrder", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除成功!', 'info',
                            function() {
                                $("#order-jd-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }
    
</script>