<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px">
    到家门店编码:&nbsp;&nbsp;<input  class="easyui-textbox" id="status_list_stationNo">
    可售状态:&nbsp;&nbsp;<input id="vendibilityCombo"/>
    <input type="hidden" id="vendibility"/>

    <button style="margin-left: 10px" class="easyui-linkbutton" iconCls="icon-search" onclick="statusListSearch()">搜索</button>
</div>
<table id="item-status-list" style="width:100%;height:800px"></table>

<div id="edit" class="easyui-dialog" data-options="closed:true">
    <form id="editForm" method="post">
        <table cellpadding="5">
            <input type="hidden" name="id">
            <tr>
                <td>到家门店编码:</td>
                <td>
                    <input class="easyui-textbox" name="stationNo" style="width: 200px;" data-options="editable:false"/>
                </td>
            </tr>
            <tr>
                <td>到家商品编码:</td>
                <td><input class="easyui-textbox" name="skuId" style="width: 200px;" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>可用库存数量:</td>
                <td><input class="easyui-numberbox" name="usableQty" data-options="min:0,max:99999999,precision:0,required:true" /></td>
            </tr>
            <tr>
                <td>可售状态:</td>
                <td>
                    <select class="easyui-combobox" name="vendibility" style="width: 100px;" data-options="editable:false">
                        <option value="0">可售</option>
                        <option value="1">不可售</option>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>

<script type="text/javascript" charset="utf-8">

    $('#vendibilityCombo').combobox({
        valueField:'key',
        textField:'value',
        data: [{
            key: '',
            value: '全部'
        },{
            key: '0',
            value: '可售'
        },{
            key: '1',
            value: '不可售'
        }],
        onSelect: function (record) {
            $("#vendibility").val(record.key);
        }
    });

    $('#item-status-list').datagrid({
        url:'/product/productStatusPage',
        title: '商品库存列表',
        pagePosition: 'top',
        singleSelect: true ,
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
                            $.post("/product/jd/asynProductStatus", null, function(data) {
                                if (data.status == 200) {
                                    $.messager.alert('提示', '商品库存同步成功!', 'info');
                                    $('#item-status-list').datagrid('reload');
                                }else{
                                    $.messager.alert('提示', '商品库存同步失败，失败原因：'+data.msg, 'warning');
                                }
                                $.messager.progress('close');
                            });
                        }
                    });
                }
            },'-',{
                text : '编辑',
                iconCls : 'icon-edit',
                handler: function(){editProduct();}
            }
        ],
        columns:[[
            {field:'id',checkbox:true},
            {field:'stationNo',title:'到家门店编码',width:100,align:'center'},
            {field:'skuId',title:'到家商品编码',width:100,align:'center'},
            {field:'usableQty',title:'可用库存数量',width:100,align:'center'},
            {field:'lockQty',title:'锁定库存数量',width:100,align:'center'},
            {field:'orderQty',title:'预占库存数量',width:100,align:'center'},
            {field:'vendibility',title:'可售状态',width:70,align:'center',
                formatter:function (value,row,index) {
                    if(value==0){
                        return "可售";
                    }else if(value==1){
                        return "不可售";
                    }
                    return value;
                }
             },
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.stationNo = $('#status_list_stationNo').val();
            param.vendibility = $("#vendibility").val();
            return true;
        }
    });

    editProduct = function () {
        var ids = getSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一条数据才能编辑!');
            return;
        }
        if (ids.indexOf(',') > 0) {
            $.messager.alert('提示', '只能选择一条数据!');
            return;
        }
        $('#edit').dialog({
            title: '编辑商品库存',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    var params = $("#editForm").serialize();
                    $.post("/product/updateProductStatus", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '修改商品库存成功!', 'info',
                                function() {
                                    $("#edit").dialog('close');
                                    $("#item-status-list").datagrid("reload");
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
        var data = $("#item-status-list").datagrid("getSelected");
        $("#editForm").form("load",data);
    }

    statusListSearch = function () {
        $('#item-status-list').datagrid('load');
    }

    getSelectionsIds = function() {
        var itemList = $("#item-status-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].stationNo+'|'+sels[i].skuId);
        }
        ids = ids.join(",");
        return ids;
    }

</script>
