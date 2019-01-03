<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table id="item-img-category-list" style="width:100%;height:800px"></table>

<div id="editImgCategoryItem" class="easyui-dialog" data-options="closed:true">
    <form id="editImgCategoryItemForm" method="post">
        <table cellpadding="5">
            <tr>
                <td>图片类目:</td>
                <td>
                    <input class="easyui-textbox" name="category" style="width: 200px;" data-options="required:true"/>
                </td>
            </tr>
            <tr>
                <td>图片类目名称:</td>
                <td><input class="easyui-textbox" name="categoryName" style="width: 200px;" data-options="required:true"/></td>
            </tr>
        </table>
    </form>
</div>

<script type="text/javascript" charset="utf-8">
    $("#item-img-category-list").datagrid({
        url:'/img/imgCategoryPage',
        title: '图片类目列表',
        pagePosition: 'top',
        singleSelect: true ,
        rownumbers: true,
        collapsible:true,
        pagination:true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
                text : '新增',
                iconCls : 'icon-add',
                handler : function() {addImgCategory();}
            }
        ],
        columns:[[
            {field:'id',title:'类目ID',width:70,align:'center'},
            {field:'category',title:'图片类目',width:200,align:'center'},
            {field:'categoryName',title:'图片类目名称',width:200,align:'center'},
            {field:'belongFile',title:'图片目录',width:100,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            return true;
        }
    });

    itemImgCategoryListSearch = function () {
        $("#item-img-category-list").datagrid('load');
    }

    addImgCategory = function () {
        $('#editImgCategoryItem').dialog({
            title: '新增类目',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    if (!$('#editImgCategoryItemForm').form('validate')) {
                        return false;
                    }
                    var params = $("#editImgCategoryItemForm").serialize();
                    $.post("/img/addImgCategory", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '新增规则成功!', 'info',
                                function() {
                                    $("#editImgCategoryItem").dialog('close');
                                    $("#item-img-category-list").datagrid("reload");
                                });
                        }else if(data.status == 201){
                            $.messager.alert('提示', data.msg, 'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#editImgCategoryItem').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editImgCategoryItemForm").form("clear");
            }
        });
    }


</script>
