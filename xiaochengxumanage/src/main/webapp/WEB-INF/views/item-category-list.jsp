<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    父ID:&nbsp;&nbsp;<input  class="easyui-textbox" id="itemCategory_search">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="itemCategoryListSearch()">搜索</button>
</div>
<table id="item-category-list" style="width:100%;height:800px"></table>

<script type="text/javascript">
    $('#item-category-list').datagrid({
        url:'/product/categoriesPage',
        title: '商品类目列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
            text : '同步',
            iconCls : 'icon-add',
            handler : function() {
                $.messager.confirm('确认对话框', '您确认要同步吗？', function(r){
                    if (r){
                        $.messager.progress();
                        $.post("/product/jd/asynCategories", null, function(data) {
                            if (data.status == 200) {
                                $.messager.alert('提示', '商品类目同步成功!', 'info');
                                $.messager.progress('close');
                                $('#item-category-list').datagrid('reload');
                            }
                        });
                    }
                });
            }
        }],
        columns:[[
            {field:'id',title:'ID',width:100,align:'center'},
            {field:'pid',title:'父ID',width:100,align:'center'},
            {field:'shopCategoryName',title:'分类名称',width:150,align:'center'},
            {field:'shopCategoryLevel',title:'分类级别',width:100,align:'center'},
            {field:'sort',title:'分类排序',width:70,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.categoryPid = $('#itemCategory_search').val();
            return true;
        }
    });
    itemCategoryListSearch = function () {
        $('#item-category-list').datagrid('load');
    }
</script>