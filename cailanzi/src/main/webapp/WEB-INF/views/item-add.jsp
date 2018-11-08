<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="padding:10px 10px 10px 10px">
	<form id="itemAddForm" class="itemForm" method="post">
	    <table cellpadding="5">
	        <tr>
	            <td>商户:</td>
	            <td>
					<input id="add_phone" name="phone" style="width: 200px;"/>
					<input id="add_belongStationNo" name="stationNo" type="hidden"/>
					<input id="add_belongStationName" name="stationName"type="hidden"/>
					<input id="add_skuId" name="skuId"type="hidden"/>
	            </td>
	        </tr>
	    </table>
	</form>
</div>
<table id="item-add-list" style="width:100%;height:700px"></table>
<script type="text/javascript" charset="utf-8">

    $('#item-add-list').datagrid({
        url:'/product/jd/addProductPage',
        title: '可添加商品列表',
        pagePosition: 'top',
        singleSelect: false ,
        rownumbers: true,
        collapsible:true,
        pagination:true,
        pageSize: 50,
        pageList:[50,100,200,500,1000],
        toolbar: [{
            text : '新增保存',
            iconCls : 'icon-save',
            handler : function() {addSave();}
        }],
        columns:[[
            {field:'id',checkbox:true},
            {field:'skuId',title:'到家商品编码',width:100,align:'center'},
            {field:'skuName',title:'商品名称',width:400,align:'center'},
            {field:'skuPrice',title:'商品价格',width:60,align:'center',
                formatter:function (value,row,index) {
                    return value/100;
                }
            },
            {field:'stockNum',title:'库存数量',width:60,align:'center'},
            {field:'imgUrl',title:'图片地址',width:200,align:'center'},
            {field:'shopCategories',title:'分类编码',width:200,align:'center'}
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.stationNo = $('#add_belongStationNo').val();
            param.phone = $('#add_phone').val();
        }
    });

    $('#add_phone').combogrid({
        panelWidth:500,
        idField:'username',
        textField:'username',
        url:'/user/comgridList',
        required:true,
        mode: 'remote',
        delay: 500,
        columns:[[
            {field:'username',title:'号码',width:100,align:'center'},
            {field:'belongStationNo',title:'到家门店编码',width:100,align:'center'},
            {field:'belongStationName',title:'门店名称',width:180,align:'center'},
            {field:'remark',title:'备注',width:100,align:'center'},
        ]],
        onSelect: function (index,row) {
			$("#add_belongStationNo").val(row.belongStationNo);
			$("#add_belongStationName").val(row.belongStationName);
            $("#item-add-list").datagrid("load");
        }
    });

    function addSave(){
        var ids = getAddItemListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '必须选择一个商品才能添加!');
            return;
        }
        $("#add_skuId").val(ids);
        var params = $("#itemAddForm").serialize();
        $.post("/product/addProduct", params, function(data) {
            if (data.status == 200) {
                $.messager.alert('提示', '新增商品成功!', 'info',
                    function() {
                        $("#item-add-list").datagrid("reload");
                    });
            }if(data.status == 400){
                $.messager.alert('警告', data.msg, 'warning');
            }
        });
	}

    getAddItemListSelectionsIds = function() {
        var itemList = $("#item-add-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].skuId);
        }
        ids = ids.join(",");
        return ids;
    }
</script>
