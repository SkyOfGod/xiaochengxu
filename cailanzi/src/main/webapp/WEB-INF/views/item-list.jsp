<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 500px;height: 40px">
    商户账号:&nbsp;&nbsp;<input  class="easyui-textbox" id="list_searchPhone">
    <button class="easyui-linkbutton" iconCls="icon-search" onclick="listSearch()">搜索</button>
</div>
<table id="item-list" style="width:100%;height:800px"></table>

<div id="edit" class="easyui-dialog" data-options="closed:true">
    <form id="editForm" method="post">
        <table cellpadding="5">
            <input type="hidden" name="id">
            <tr>
                <td>商户:</td>
                <td>
                    <input class="easyui-textbox" name="phone" style="width: 300px;" data-options="editable:false"/>
                </td>
            </tr>
            <tr>
                <td>商品类型:</td>
                <td><input class="easyui-textbox" name="name" style="width: 300px;" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>商品价格:</td>
                <td>
                    <input class="easyui-numberbox" type="text" name="initPrice" data-options="min:1,max:99999999,precision:2,required:true" />
                    <input type="hidden" name="price">
                </td>
            </tr>
            <tr>
                <td>库存数量:</td>
                <td><input class="easyui-numberbox" type="text" name="storeNum" data-options="min:1,max:99999999,precision:0,required:true" /></td>
            </tr>
            <tr>
                <td>商品描述:</td>
                <td><input class="easyui-textbox" name="description" data-options="multiline:true,validType:'length[0,150]'" style="height:60px;width: 280px;"></input></td>
            </tr>
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
            {field:'name',title:'商品名称',width:400,align:'center'},
            {field:'shopCategories',title:'分类编码',width:100,align:'center'},
            {field:'price',title:'商品价格',width:70,align:'center',
                formatter:function (value,row,index) {
                    return value/100;
                }
             },
            {field:'storeNum',title:'库存数量',width:100,align:'center'},
            {field:'description',title:'商品描述',width:200,align:'center'},
            {field:'imgUrl',title:'图片地址',width:60,align:'center'},
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
            return true;
        }
    });

    editProduct = function () {
        var ids = getSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一个商品才能编辑!');
            return;
        }
        if (ids.indexOf(',') > 0) {
            $.messager.alert('提示', '只能选择一个商品!');
            return;
        }
        $('#edit').dialog({
            title: '编辑商品',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    $("#editForm [name=price]").val(eval($("#editForm [name=initPrice]").val()) * 100);
                    var params = $("#editForm").serialize();
                    $.post("/product/updateProduct", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '修改商品成功!', 'info',
                                function() {
                                    $("#edit").dialog('close');
                                    $("#item-list").datagrid("reload");
                                });
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#edit').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#editForm").form("clear");
            }
        });
        var data = $("#item-list").datagrid("getSelected");
        data.initPrice = data.price/100;
        $("#editForm").form("load",data);
    }

    deleteProduct = function () {
        var ids = getSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中商品!');
            return;
        }
        $.messager.confirm('确认', '确定删除ID为 ' + ids + ' 的商品吗？', function(r) {
            if (r) {
                var params = {"ids" : ids};
                $.post("/product/deleteProduct", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除商品成功!', 'warning',
                            function() {
                                $("#item-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }

    listSearch = function () {
        $('#item-list').datagrid('load');
    }

    getSelectionsIds = function() {
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
