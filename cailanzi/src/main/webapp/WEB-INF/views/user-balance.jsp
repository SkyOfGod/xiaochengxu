<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 800px;height: 40px">
	商户:&nbsp;&nbsp;<input class="easyui-textbox" id="userBalanceList_searchUsername">
	<button class="easyui-linkbutton" iconCls="icon-search" onclick="userBalanceSearch()">搜索</button>
</div>
<table id="user-balance-day-list" style="width:100%;height:700px"></table>

<div id="addUserBalanceDialog" class="easyui-dialog" data-options="closed:true">
	<form id="addUserBalanceForm" method="post">
		<table cellpadding="5">
			<tr>
				<td>商户:</td>
				<td>
					<input name="username" data-options="required:true" style="width: 300px;"/>
				</td>
			</tr>
			<tr>
				<td>金额:</td>
				<td>
					<input class="easyui-numberbox" name="price" data-options="required:true" style="width: 280px;"/>元
				</td>
			</tr>
			<tr>
				<td>备注:</td>
				<td>
					<input class="easyui-textbox" name="remark" style="width: 300px;"/>
				</td>
			</tr>
		</table>
	</form>
</div>
<script type="text/javascript">

    $("#user-balance-day-list").datagrid({
        url:'/userBalanceDay/listPage',
        title: '商户余额列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
            text : '充值',
            iconCls : 'icon-add',
            handler : function() {addUserBalance();}
        }],
        columns:[[
            {field:'id',checkbox:true},
            {field:'username',title:'商户',width:100,align:'center'},
            {field:'balance',title:'余额(元)',width:100,align:'center',
                formatter: function(value,row,index){ return value/100; }
			},
            {field:'yesterdayBill',title:'昨日账单(元)',width:100,align:'center',
                formatter: function(value,row,index){
                	if(value){
                        return value/100;
                    }
            	}
			},
            {field:'createDate',title:'创建时间',width:150,align:'center'},
            {field:'updateTime',title:'跟新时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.username = $('#userBalanceList_searchUsername').val();
            return true;
        }
    });

    addUserBalanceCombogrid = function () {
        $('#addUserBalanceForm [name=username]').combogrid({
            panelWidth:500,
            idField:'username',
            textField:'username',
            url:'/user/comgridList',
            mode: 'remote',
            delay: 500,
            multiple: true,
            columns:[[
                {field:'username',title:'号码',width:100,align:'center'},
                {field:'belongStationNo',title:'到家门店编码',width:100,align:'center'},
                {field:'belongStationName',title:'门店名称',width:180,align:'center'},
                {field:'remark',title:'备注',width:100,align:'center'},
            ]],
        });

    }

    addUserBalance = function () {
        $('#addUserBalanceDialog').dialog({
            title: '新增规则产品',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    if (!$('#addUserBalanceForm').form('validate')) {
                        return false;
                    }
                    var params = $("#addUserBalanceForm").serialize();
                    $.post("/userBalanceDay/addUserBalance", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '新增成功!', 'info',
                                function() {
                                    $("#addUserBalanceDialog").dialog('close');
                                    $("#user-balance-day-list").datagrid("reload");
                                });
                        }else if(data.status == 400){
                            $.messager.alert('警告',data.msg,'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#addUserBalanceDialog').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#addUserBalanceDialog").form("clear");
            }
        });
        addUserBalanceCombogrid();
    }

    userBalanceSearch = function () {
        $('#user-balance-day-list').datagrid('load');
    }

</script>