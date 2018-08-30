<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 500px;height: 40px">
	到家门店编码:&nbsp;&nbsp;<input  class="easyui-textbox" id="searchNo">
	<button class="easyui-linkbutton" iconCls="icon-search" onclick="userSearch()">搜索</button>
</div>
<table id="user-list" style="width:100%;height:800px"></table>

<div id="addDialog" class="easyui-dialog" data-options="closed:true">
	<form id="addForm" method="post">
		<table cellpadding="5">
			<tr>
				<td>用户名:</td>
				<td>
					<input class="easyui-textbox" type="text" name="username" data-options="required:true" style="width: 240px;"/>
					<input type="hidden" name="price">
				</td>
			</tr>
			<tr>
				<td>密码:</td>
				<td><input class="easyui-textbox" type="text" name="password" data-options="required:true" style="width: 240px;"/></td>
			</tr>
			<tr>
				<td>所属门店:</td>
				<td>
					<input id="belongStationNo" name="belongStationNo" style="width: 240px;"/>
					<input id="belongStationName" type="hidden" name="belongStationName">
				</td>
			</tr>
			<tr>
				<td>类型:</td>
				<td>
					<select class="easyui-combobox" name="type" style="width: 100px;" data-options="editable:false">
						<option value="1">用户</option>
						<option value="0">管理者</option>
					</select>
				</td>
			</tr>
		</table>
	</form>
</div>
<script type="text/javascript">

    $('#belongStationNo').combogrid({
        panelWidth:520,
        idField:'stationNo',
        textField:'stationName',
        url:'/shop/compgirdList',
        mode: 'remote',
        delay: 500,
        columns:[[
            {field:'phone',title:'号码',width:100,align:'center'},
            {field:'stationNo',title:'到家门店编码',width:100,align:'center'},
            {field:'stationName',title:'门店名称',width:180,align:'center'},
            {field:'cityName',title:'城市名称',width:60,align:'center'},
            {field:'countyName',title:'镇名称',width:60,align:'center'}
        ]],
        onSelect: function (index,row) {
            $("#belongStationName").val(row.stationName);
        }
    });

    $('#user-list').datagrid({
        url:'/user/userPage',
        title: '用户列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
            text : '新增',
            iconCls : 'icon-add',
            handler : function() {addUser();}
        },'-',
            {
                text : '删除',
                iconCls : 'icon-cancel',
                handler : function() {deleteUser();}
            },
        ],
        columns:[[
            {field:'id',checkbox:true},
            {field:'username',title:'账号',width:100,align:'center'},
            {field:'password',title:'密码',width:300,align:'center'},
            {field:'belongStationNo',title:'所属到家门店编码',width:200,align:'center'},
            {field:'belongStationName',title:'所属门店名称',width:200,align:'center'},
            {field:'type',title:'用户类别',width:70,align:'center',
                formatter:function (value,row,index) {
                	if(value==0){
                	    return "管理员";
					}else if(value==1){
                	    return "用户";
					}
                    return value;
                }
            },
        ]],
        onBeforeLoad: function (param) {
            param.belongStationNo = $('#searchNo').val();
            return true;
        }
    });

    addUser = function () {
        $('#addDialog').dialog({
            title: '新增用户',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    if (!$('#addForm').form('validate')) {
                        return false;
                    }
                    var params = $("#addForm").serialize();
                    $.post("/user/addUser", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '新增用户成功!', 'info',
                                function() {
                                    $("#addDialog").dialog('close');
                                    $("#user-list").datagrid("reload");
                                });
                        }else if(data.status == 400){
                            $.messager.alert('警告',data.msg,'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#addDialog').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#addDialog").form("clear");
            }
        });
    }

    deleteUser = function () {
        var ids = getSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中数据!');
            return;
        }
        $.messager.confirm('确认', '确定删除ID为 ' + ids + ' 的用户数据吗？', function(r) {
            if (r) {
                var params = {"ids" : ids};
                $.post("/user/deleteUser", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除用户成功!', 'info',
                            function() {
                                $("#user-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }

    userSearch = function () {
        $('#user-list').datagrid('load');
    }

    getSelectionsIds = function() {
        var itemList = $("#user-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].id);
        }
        ids = ids.join(",");
        return ids;
    }
</script>