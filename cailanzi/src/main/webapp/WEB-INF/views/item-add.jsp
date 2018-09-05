<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="padding:10px 10px 10px 10px">
	<form id="itemAddForm" class="itemForm" method="post">
	    <table cellpadding="5">
	        <tr>
	            <td>商户:</td>
	            <td>
					<input id="add_phone" name="phone" style="width: 300px;"/>
					<input id="add_belongStationNo" name="belongStationNo" type="hidden"/>
					<input id="add_belongStationName" name="belongStationName"type="hidden"/>
	            </td>
	        </tr>
	        <tr>
	            <td>商品类型:</td>
	            <td>
					<input id="add_product" name="name" class="easyui-combogrid" style="width: 300px;"/>
					<input id="add_skuId" type="hidden" name="skuId">
					<input id="add_shopCategories" type="hidden" name="shopCategories">
				</td>
	        </tr>
	        <tr>
	            <td>商品价格:</td>
	            <td>
					<input type="text" name="initPrice" data-options="editable:false" />
					<input type="hidden" name="price">
	            </td>
	        </tr>
	        <tr>
	            <td>库存数量:</td>
	            <td><input type="text" name="storeNum" data-options="editable:false" /></td>
	        </tr>
	        <tr>
	            <td>商品图片:</td>
	            <td>
	            	 <%--<a href="javascript:void(0)" class="easyui-linkbutton picFileUpload">上传图片</a>--%>
	                 <input type="file" name="file"/>
	            </td>
	        </tr>
			<tr>
				<td>商品描述:</td>
				<td><input class="easyui-textbox" name="description" data-options="multiline:true,validType:'length[0,150]'" style="height:60px;width: 280px;"></input></td>
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
        panelWidth:400,
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
        ]],
        onSelect: function (index,row) {
			$("#add_belongStationNo").val(row.belongStationNo);
			$("#add_belongStationName").val(row.belongStationName);
            getProductCombogrid(row.belongStationNo);
        }
    });

    getProductCombogrid = function (belongStationNo) {
        $('#add_product').combogrid({
            panelWidth:760,
            idField:'skuName',
            textField:'skuName',
            url:'/product/jd/comgridList',
            mode: 'remote',
            delay: 500,
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
                {field:'shopCategories',title:'分类编码',width:120,align:'center'}
            ]],
            onSelect: function (index,row) {
                $("#add_skuId").val(row.skuId);
                $("#add_shopCategories").val(row.shopCategories);
                $("#itemAddForm [name=initPrice]").val(row.skuPrice/100);
                $("#itemAddForm [name=storeNum]").val(row.stockNum);
            },
            onBeforeLoad: function (param) {
                param.belongStationNo = belongStationNo;
            }
        });
    }


    function submitForm() {
        //表单校验
        if (!$('#itemAddForm').form('validate')) {
            return false;
        }
        //转化价格单位，将元转化为分
        var price = eval($("#itemAddForm [name=initPrice]").val()) * 100;
        $("#itemAddForm [name=price]").val(parseInt(price));
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
