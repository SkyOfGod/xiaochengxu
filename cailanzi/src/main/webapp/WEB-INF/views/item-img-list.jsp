<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="/js/common.js"></script>
<link href="/js/kindeditor-4.1.10/themes/default/default.css" type="text/css" rel="stylesheet">
<script type="text/javascript" charset="utf-8" src="/js/kindeditor-4.1.10/kindeditor-all-min.js"></script>
<div style="width: 100%;height: 40px">
    图片名称:&nbsp;&nbsp;<input  class="easyui-textbox" id="list_searchImgName">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="itemImgListSearch()">搜索</button>
</div>
<table id="item-img-list" style="width:100%;height:700px"></table>

<div id="editItemImg" class="easyui-dialog" data-options="closed:true">
    <form id="editItemImgForm" method="post" enctype="multipart/form-data">
        <table cellpadding="5">
            <input type="hidden" name="id">
            <tr>
                <td>图片类目:</td>
                <td>
                    <input name="categoryId" style="width: 200px;" data-options="required:true"/>
                </td>
            </tr>
            <tr>
                <td>图片:</td>
                <td>
                    <input type="file" name="file" data-options="required:true">
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="updateItemImg" class="easyui-dialog" data-options="closed:true">
    <form id="updateItemImgForm" method="post" enctype="multipart/form-data">
        <table cellpadding="5">
            <input type="hidden" name="id">
            <tr>
                <td>名称:</td>
                <td>
                    <input class="easyui-textbox" name="name" style="width: 200px;" data-options="required:true"/>
                </td>
            </tr>
            <tr>
                <td>图片地址:</td>
                <td>
                    <input class="easyui-textbox" name="address" style="width: 200px;" data-options="editable:false">
                </td>
            </tr>
        </table>
    </form>
</div>

<script type="text/javascript" charset="utf-8">

    imgCategoryComgrid = function () {
        $("#editItemImgForm [name=categoryId]").combogrid({
            panelWidth:350,
            idField:'id',
            textField:'categoryName',
            url:'/img/imgCategoryComgrid',
            required:true,
            mode: 'remote',
            delay: 500,
            columns:[[
                {field:'id',title:'ID',width:30,align:'center'},
                {field:'category',title:'商品类目',width:100,align:'center'},
                {field:'categoryName',title:'商品类目名称',width:100,align:'center'},
                {field:'belongFile',title:'所属文件夹',width:100,align:'center'},
            ]],
        });
    }

    $("#item-img-list").datagrid({
        url:'/img/imgPage',
        title: '图片列表',
        pagePosition: 'top',
        singleSelect: false ,
        rownumbers: true,
        collapsible: true,
        pagination: true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
                text : '上传',
                iconCls : 'icon-add',
                handler : function() {addImg();}
            },'-',{
                text : '编辑',
                iconCls : 'icon-edit',
                handler: function(){editImg();}
            },'-',
            {
                text : '删除',
                iconCls : 'icon-cancel',
                handler : function() {deleteImg();}
            },
        ],
        columns:[[
            {field:'id',checkbox:true},
            {field:'categoryId',title:'类目ID',width:70,align:'center'},
            {field:'name',title:'图片名称',width:300,align:'center'},
            {field:'address',title:'图片地址',width:400,align:'center',
                formatter: function(value,row,index){
                    if(value){
                        return 'https://api.1000heng.xyz'+value;
                    }
                }
            },
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.name = $('#list_searchImgName').val();
            return true;
        }
    });

    addImg = function () {
        $("#editItemImg").dialog({
            title: '上传图片',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    var params = $("#editItemImgForm").serialize();
                    $('#editItemImgForm').form('submit', {
                            url:'/pic/upload',
                            onSubmit: function(param){
                                /*if (!$(this).form('validate')) {
                                    return false;
                                }*/
                            },
                            success:function(data){
                                var _data = eval('(' + data + ')');
                                if (_data.status == 200) {
                                    $.messager.alert('提示', '图片上传成功!', 'info',
                                        function() {
                                            $("#editItemImg").dialog('close');
                                            $("#item-img-list").datagrid("reload");
                                        });
                                }else if(_data.status == 201){
                                    $.messager.alert('提示', data.msg, 'warning');
                                }
                            }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$("#editItemImg").dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editItemImgForm").form("clear");
            }
        });
        imgCategoryComgrid();
    }

    editImg = function () {
        var ids = getItemImgListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一条数据才能编辑!');
            return;
        }
        if (ids.indexOf(',') > 0) {
            $.messager.alert('提示', '只能选择一条数据!');
            return;
        }
        $("#updateItemImg").dialog({
            title: '编辑图片名称',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    var params = $("#updateItemImgForm").serialize();
                    $.post("/img/updateImgName", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '修改成功!', 'info',
                                function() {
                                    $("#updateItemImg").dialog('close');
                                    $("#item-img-list").datagrid("reload");
                                });
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$("#updateItemImg").dialog("close");}
            }],
            onBeforeClose: function () {
                $("#updateItemImgForm").form("clear");
            }
        });
        var data = $("#item-img-list").datagrid("getSelected");
        $("#updateItemImgForm").form("load",data);
    }

    deleteImg = function () {
        var ids = getItemImgListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中数据!');
            return;
        }
        $.messager.confirm('确认', '确定删除选中数据吗？', function(r) {
            if (r) {
                var params = {"ids" : ids};
                $.post("/img/deleteImg", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除成功!', 'info',
                            function() {
                                $("#item-img-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }

    itemImgListSearch = function () {
        $("#item-img-list").datagrid('load');
    }

    getItemImgListSelectionsIds = function() {
        var itemList = $("#item-img-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].id);
        }
        ids = ids.join(",");
        return ids;
    }

</script>
