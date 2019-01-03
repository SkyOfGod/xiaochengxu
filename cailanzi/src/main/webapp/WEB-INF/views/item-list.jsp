<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    商户账号:&nbsp;&nbsp;<input  class="easyui-textbox" id="list_searchPhone">
    商品名称:&nbsp;&nbsp;<input  class="easyui-textbox" id="list_searchSkuName">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="itemListSearch()">搜索</button>
</div>
<table id="item-list" style="width:100%;height:700px"></table>

<div id="editItem" class="easyui-dialog" data-options="closed:true">
    <form id="editItemForm" method="post">
        <table cellpadding="5">
            <input type="hidden" name="id">
            <input type="hidden" name="belongStationNo">
            <input type="hidden" name="skuId">
            <tr>
                <td>商户:</td>
                <td>
                    <input class="easyui-textbox" name="phone" style="width: 300px;" data-options="editable:false"/>
                </td>
            </tr>
            <tr>
                <td>商品名称:</td>
                <td><input class="easyui-textbox" name="name" style="width: 300px;" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>商品价格:</td>
                <td>
                    <input class="easyui-numberbox" type="text" name="price" data-options="min:1,max:99999999,precision:0,required:true" />分
                </td>
            </tr>
            <tr>
                <td>库存数量:</td>
                <td><input class="easyui-numberbox" type="text" name="storeNum" data-options="min:1,max:99999999,precision:0,required:true" />个</td>
            </tr>
            <tr>
                <td>可售状态:</td>
                <td>
                    <select class="easyui-combobox" name="isSell" style="width: 100px;" data-options="editable:false">
                        <option value="0">可售</option>
                        <option value="1">不可售</option>
                    </select>
                </td>
            </tr>
            <%--<tr>
                <td>商品描述:</td>
                <td><input class="easyui-textbox" name="description" data-options="multiline:true,validType:'length[0,150]'" style="height:60px;width: 280px;"></input></td>
            </tr>--%>
        </table>
    </form>
</div>

<script type="text/javascript" charset="utf-8">
    $('#item-list').datagrid({
        url:'/product/productPage',
        title: '商品列表',
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
                handler : function() {$(".tree-title:contains('新增商品')").parent().click();}
            },'-',{
                text : '编辑',
                iconCls : 'icon-edit',
                handler: function(){editProduct();}
            },'-',
            {
                text : '删除',
                iconCls : 'icon-cancel',
                handler : function() {deleteProduct();}
            },
        ],
        columns:[[
            {field:'id',checkbox:true},
            {field:'phone',title:'商户账号',width:100,align:'center'},
            {field:'belongStationNo',title:'门店编码',width:100,align:'center'},
            {field:'belongStationName',title:'门店名称',width:180,align:'center'},
            {field:'skuId',title:'到家商品编码',width:100,align:'center'},
            {field:'name',title:'商品名称',width:450,align:'center'},
            {field:'shopCategories',title:'分类编码',width:100,align:'center'},
            {field:'price',title:'商品价格(分)',width:70,align:'center'},
            {field:'storeNum',title:'库存数量',width:100,align:'center'},
            /*{field:'description',title:'商品描述',width:200,align:'center'},*/
            {field:'isSell',title:'是否可售',width:60,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '可售';
                    } else if(value=1){
                        return '不可售';
                    } else {
                        return value;
                    }
                }
            },
            {field:'createTime',title:'创建时间',width:150,align:'center'},
            {field:'updateTime',title:'修改时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.phone = $('#list_searchPhone').val();
            param.skuName = $('#list_searchSkuName').val();
            return true;
        }
    });

    editProduct = function () {
        var ids = getItemListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一个商品才能编辑!');
            return;
        }
        if (ids.indexOf(',') > 0) {
            $.messager.alert('提示', '只能选择一个商品!');
            return;
        }
        $("#editItem").dialog({
            title: '编辑商品',
            width: 450,
            height: 400,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    var params = $("#editItemForm").serialize();
                    $.post("/product/updateProductOfStorePriceVendibility", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '修改商品成功!', 'info',
                                function() {
                                    $("#editItem").dialog('close');
                                    $("#item-list").datagrid("reload");
                                });
                        }else if(data.status == 201){
                            $.messager.alert('提示', data.msg, 'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$("#editItem").dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editItemForm").form("clear");
            }
        });
        var data = $("#item-list").datagrid("getSelected");
        $("#editItemForm").form("load",data);
    }

    deleteProduct = function () {
        var ids = getItemListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中商品!');
            return;
        }
        $.messager.confirm('确认', '确定删除选中的商品吗？', function(r) {
            if (r) {
                var params = {"ids" : ids};
                $.post("/product/deleteProduct", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除商品成功!', 'info',
                            function() {
                                $("#item-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }

    itemListSearch = function () {
        $('#item-list').datagrid('load');
    }

    getItemListSelectionsIds = function() {
        var itemList = $("#item-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].id);
        }
        ids = ids.join(",");
        return ids;
    }

</script>
