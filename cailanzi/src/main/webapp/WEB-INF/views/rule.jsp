<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    名称:&nbsp;&nbsp;<input  class="easyui-textbox" id="ruleList_searchName">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="ruleListSearch()">搜索</button>
</div>
<table id="rule-list" style="width:100%;height:800px"></table>

<div id="editRule" class="easyui-dialog" data-options="closed:true">
    <form id="editRuleForm" method="post">
        <table cellpadding="3">
            <input type="hidden" name="id">
            <tr>
                <td>名称:</td>
                <td>
                    <input class="easyui-textbox" name="name" style="width: 200px;" data-options="required:true"/>
                </td>
            </tr>
            <tr>
                <td>比率:</td>
                <td><input class="easyui-numberbox" name="rate" style="width: 200px;" data-options="required:true"/></td>
            </tr>
            <tr>
                <td>是否有效:</td>
                <td>
                    <select class="easyui-combobox" name="isValid" style="width: 100px;" data-options="editable:false">
                        <option value="0">有效</option>
                        <option value="1">无效</option>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>

<script type="text/javascript" charset="utf-8">
    $('#rule-list').datagrid({
        url:'/rule/rulePage',
        title: '规则列表',
        pagePosition: 'top',
        singleSelect: false ,
        rownumbers: true,
        collapsible:true,
        pagination:true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
                text : '新增',
                iconCls : 'icon-add',
                handler : function() {addRule();}
            },'-',{
                text : '编辑',
                iconCls : 'icon-edit',
                handler: function(){editRule();}
            },'-',
            {
                text : '删除',
                iconCls : 'icon-cancel',
                handler : function() {deleteRule();}
            },
        ],
        columns:[[
            {field:'id',checkbox:true},
            {field:'name',title:'名称',width:200,align:'center'},
            {field:'rate',title:'比率',width:100,align:'center',
                formatter:function (value,row,index) {
                    return value+"%";
                }
            },
            {field:'isValid',title:'是否有效',width:100,align:'center',
                formatter:function (value,row,index) {
                    if(value==0){
                        return "有效";
                    }else if(value==1){
                        return "无效";
                    }
                    return value;
                }
            },
            {field:'createTime',title:'创建时间',width:150,align:'center'},
            {field:'updateTime',title:'修改时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.name = $('#ruleList_searchName').val();
            return true;
        }
    });

    addRule = function () {
        $('#editRule').dialog({
            title: '新增规则',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    if (!$('#editRuleForm').form('validate')) {
                        return false;
                    }
                    var params = $("#editRuleForm").serialize();
                    $.post("/rule/addRule", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '新增规则成功!', 'info',
                                function() {
                                    $("#editRule").dialog('close');
                                    $("#rule-list").datagrid("reload");
                                });
                        }else if(data.status == 201){
                            $.messager.alert('提示', data.msg, 'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#editRule').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editRuleForm").form("clear");
            }
        });
    }

    editRule = function () {
        var ids = getRuleListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一个商品才能编辑!');
            return;
        }
        if (ids.indexOf(',') > 0) {
            $.messager.alert('提示', '只能选择一个商品!');
            return;
        }
        $('#editRule').dialog({
            title: '编辑规则',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    var params = $("#editRuleForm").serialize();
                    $.post("/rule/updateRule", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '修改商品成功!', 'info',
                                function() {
                                    $("#editRule").dialog('close');
                                    $("#rule-list").datagrid("reload");
                                });
                        }else if(data.status == 201){
                            $.messager.alert('提示', data.msg, 'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#editRule').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editRuleForm").form("clear");
            }
        });
        var data = $("#rule-list").datagrid("getSelected");
        $("#editRuleForm").form("load",data);
    }

    deleteRule = function () {
        var ids = getRuleListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中商品!');
            return;
        }
        $.messager.confirm('确认', '删除ID为 ' + ids + ' 的数据将会删除关联的商品数据？', function(r) {
            if (r) {
                var params = {"ids" : ids};
                $.post("/rule/deleteRule", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除商品成功!', 'warning',
                            function() {
                                $("#rule-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }

    ruleListSearch = function () {
        $('#rule-list').datagrid('load');
    }

    getRuleListSelectionsIds = function() {
        var itemList = $("#rule-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].id);
        }
        ids = ids.join(",");
        return ids;
    }

</script>
