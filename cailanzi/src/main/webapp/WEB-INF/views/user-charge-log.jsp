<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 800px;height: 40px">
	商户:&nbsp;&nbsp;<input class="easyui-textbox" id="userChargeLogList_searchUsername">
	<button class="easyui-linkbutton" iconCls="icon-search" onclick="userChargeLogSearch()">搜索</button>
</div>
<table id="user-charge-log-list" style="width:100%;height:700px"></table>

<script type="text/javascript">

    $("#user-charge-log-list").datagrid({
        url:'/userChargeLog/listPage',
        title: '商户余额列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        columns:[[
            {field:'id',checkbox:true},
            {field:'username',title:'商户',width:100,align:'center'},
            {field:'charge',title:'充值金额(元)',width:100,align:'center',
                formatter: function(value,row,index){ return value/100; }
            },
            {field:'remark',title:'备注',width:100,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.username = $('#userChargeLogList_searchUsername').val();
            return true;
        }
    });

    userChargeLogSearch = function () {
        $('#user-charge-log-list').datagrid('load');
    }

</script>