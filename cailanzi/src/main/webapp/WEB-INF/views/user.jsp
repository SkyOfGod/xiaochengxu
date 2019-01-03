<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 500px;height: 40px">
	到家门店编码:&nbsp;&nbsp;<input  class="easyui-textbox" id="searchNo">
	<button class="easyui-linkbutton" iconCls="icon-search" onclick="userSearch()">搜索</button>
</div>
<table id="user-list" style="width:100%;height:700px"></table>

<div id="addUserDialog" class="easyui-dialog" data-options="closed:true">
	<form id="addUserForm" method="post">
		<table cellpadding="5">
			<tr>
				<td>用户名:</td>
				<td>
					<input class="easyui-textbox" type="text" name="username" data-options="required:true" style="width: 240px;"/>
				</td>
			</tr>
			<tr>
				<td>密码:</td>
				<td><input class="easyui-textbox" type="text" name="password" data-options="required:true" style="width: 240px;"/></td>
			</tr>
			<tr>
				<td>所属门店:</td>
				<td>
					<input id="belongStationNo" name="belongStationNo" data-options="required:true"  style="width: 240px;"/>
					<input id="belongStationName" type="hidden" name="belongStationName">
				</td>
			</tr>
			<tr>
				<td>类型:</td>
				<td>
					<select class="easyui-combobox" name="type" style="width: 100px;" data-options="editable:false">
						<option value="1">备货员</option>
						<option value="2">收货员</option>
						<option value="0">管理者</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>备注:</td>
				<td><input class="easyui-textbox" type="text" name="remark"  style="width: 240px;"/></td>
			</tr>
		</table>
	</form>
</div>

<div id="editUserDialog" class="easyui-dialog" data-options="closed:true">
	<form id="editUserForm" method="post">
		<table cellpadding="5">
			<tr>
				<td>用户名:</td>
				<td>
					<input type="hidden" name="id">
					<input class="easyui-textbox" type="text" name="username" style="width: 240px;"/>
				</td>
			</tr>
			<tr>
				<td>备注:</td>
				<td><input class="easyui-textbox" type="text" name="remark"  style="width: 240px;"/></td>
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
        },'-',{
            text : '编辑',
            iconCls : 'icon-edit',
            handler: function(){editUser();}
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
            {field:'type',title:'用户类别',width:100,align:'center',
                formatter:function (value,row,index) {
                	if(value==0){
                	    return "后台管理员";
					}else if(value==1){
                	    return "备货员";
					}else if(value==2){
					    return "收货员";
					}
                    return value;
                }
            },
            {field:'remark',title:'备注',width:200,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.belongStationNo = $('#searchNo').val();
            return true;
        }
    });

    addUser = function () {
        $('#addUserDialog').dialog({
            title: '新增用户',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    if (!$('#addUserForm').form('validate')) {
                        return false;
                    }
                    var params = $("#addUserForm").serialize();
                    $.post("/user/addUser", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '新增用户成功!', 'info',
                                function() {
                                    $("#addUserDialog").dialog('close');
                                    $("#user-list").datagrid("reload");
                                });
                        }else if(data.status == 201){
                            $.messager.alert('警告',data.msg,'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#addUserDialog').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#addUserDialog").form("clear");
            }
        });
    }

    editUser = function () {
        var ids = getUserListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一条才能编辑!');
            return;
        }
        if (ids.indexOf(',') > 0) {
            $.messager.alert('提示', '只能选择一条数据!');
            return;
        }
        $("#editUserDialog").dialog({
            title: '编辑用户',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    var params = $("#editUserForm").serialize();
                    $.post("/user/editUser", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '修改商品成功!', 'info',
                                function() {
                                    $("#editUserDialog").dialog('close');
                                    $("#user-list").datagrid("reload");
                                });
                        }else if(data.status == 201){
                            $.messager.alert('提示', data.msg, 'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$("#editUserDialog").dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editUserForm").form("clear");
            }
        });
        var data = $("#user-list").datagrid("getSelected");
        $("#editUserForm").form("load",data);
    }

    deleteUser = function () {
        var ids = getUserListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中数据!');
            return;
        }
        $.messager.confirm('确认', '删除选中用户的数据将会删除关联的商品数据？', function(r) {
            if (r) {
                var params = {"names" : ids};
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

    getUserListSelectionsIds = function() {
        var itemList = $("#user-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].username);
        }
        ids = ids.join(",");
        return ids;
    }
</script>