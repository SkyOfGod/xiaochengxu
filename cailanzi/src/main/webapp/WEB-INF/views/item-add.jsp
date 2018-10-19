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
	            </td>
	        </tr>
	        <tr>
	            <td>商品类型:</td>
	            <td>
					<input id="add_product" name="skuId" class="easyui-combogrid" style="width: 400px;"/>
				</td>
	        </tr>
	    </table>
	</form>
	<div style="padding:5px">
	    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">提交</a>
	    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">重置</a>
	</div>
</div>
<script type="text/javascript" charset="utf-8">

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
            getProductCombogrid(row.belongStationNo,row.username);
        }
    });

    getProductCombogrid = function (belongStationNo,username) {
        $('#add_product').combogrid({
            panelWidth:960,
            idField:'skuId',
            textField:'skuId',
            url:'/product/jd/comgridList',
            mode: 'remote',
            delay: 500,
            multiple: true,
            required:true,
            columns:[[
                {field:'skuId',title:'到家商品编码',width:100,align:'center'},
                {field:'skuName',title:'商品名称',width:400,align:'center'},
                {field:'skuPrice',title:'商品价格',width:60,align:'center',
                    formatter:function (value,row,index) {
                        return value/100;
                    }
				},
                {field:'stockNum',title:'库存数量',width:60,align:'center'},
                {field:'imgUrl',title:'图片地址',width:200,align:'center'},
                {field:'shopCategories',title:'分类编码',width:120,align:'center'}
            ]],
            onSelect: function (index,row) {
            },
            onBeforeLoad: function (param) {
                param.belongStationNo = belongStationNo;
                param.username = username;
            }
        });
    }

    function submitForm() {
        //表单校验
        if (!$('#itemAddForm').form('validate')) {
            return false;
        }
        var params = $("#itemAddForm").serialize();
        $.post("/product/addProduct", params, function(data) {
            if (data.status == 200) {
                $.messager.alert('提示', '新增商品成功!', 'info',
                    function() {
                        $('#itemAddForm').form('reset');
                    });
            }if(data.status == 400){
                $.messager.alert('警告', data.msg, 'warning');
			}
        });

    }

    function clearForm() {
        $('#itemAddForm').form('reset');
    }
</script>
