<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 800px;height: 40px">
	组名称:&nbsp;&nbsp;<input class="easyui-textbox" id="ruleProductList_searchRuleName">
	商品名称:&nbsp;&nbsp;<input class="easyui-textbox" id="ruleProductList_searchSkuName">
	<button class="easyui-linkbutton" iconCls="icon-search" onclick="userSearch()">搜索</button>
</div>
<table id="rule-product-list" style="width:100%;height:800px"></table>

<div id="addRuleProductDialog" class="easyui-dialog" data-options="closed:true">
	<form id="addRuleProductForm" method="post">
		<table cellpadding="5">
			<tr>
				<td>组名称:</td>
				<td>
					<input id="rule_combogrid" name="ruleName" data-options="required:true" style="width: 300px;"/>
					<input type="hidden" name="ruleId">
				</td>
			</tr>
			<tr>
				<td>商品名称:</td>
				<td>
					<input id="sku_combgrid" name="skuName" data-options="required:true" style="width: 300px;"/>
					<input type="hidden" name="skuId">
				</td>
			</tr>
		</table>
	</form>
</div>
<script type="text/javascript">

    $('#rule-product-list').datagrid({
        url:'/ruleProduct/ruleProductPage',
        title: '规则商品列表',
        pagePosition: 'top',
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        rownumbers: true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
            text : '新增',
            iconCls : 'icon-add',
            handler : function() {addRuleProduct();}
        },'-',
            {
                text : '删除',
                iconCls : 'icon-cancel',
                handler : function() {deleteRuleProduct();}
            },
        ],
        columns:[[
            {field:'id',checkbox:true},
            {field:'ruleId',title:'组ID',width:100,align:'center'},
            {field:'ruleName',title:'组名称',width:200,align:'center'},
            {field:'skuId',title:'到家商品编码',width:200,align:'center'},
            {field:'skuName',title:'商品名称',width:300,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.ruleName = $('#ruleProductList_searchRuleName').val();
            param.skuName = $('#ruleProductList_searchSkuName').val();
            return true;
        }
    });

    $('#rule_combogrid').combogrid({
        panelWidth:400,
        idField:'name',
        textField:'name',
        url:'/ruleProduct/ruleCompgird',
        mode: 'remote',
        delay: 500,
        columns:[[
            {field:'id',title:'组ID',width:100,align:'center'},
            {field:'name',title:'组名称',width:100,align:'center'},
            {field:'rate',title:'比率',width:180,align:'center',
                formatter:function (value,row,index) {
                    return value+"%";
                }
			},
        ]],
        onSelect: function (index,row) {
            $("#addRuleProductForm [name=ruleId]").val(row.id);
        }
    });

    $('#sku_combgrid').combogrid({
        panelWidth:600,
        idField:'skuName',
        textField:'skuName',
        url:'/ruleProduct/productJdComgrid',
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
        ]],
        onSelect: function (index,row) {
            $("#addRuleProductForm [name=skuId]").val(row.skuId);
        },
    });

    addRuleProduct = function () {
        $('#addRuleProductDialog').dialog({
            title: '新增规则产品',
            width: 400,
            height: 300,
            closed: false,
            cache: false,
            modal: true,
            buttons:[{
                text:'保存',
                handler:function(){
                    if (!$('#addRuleProductForm').form('validate')) {
                        return false;
                    }
                    var params = $("#addRuleProductForm").serialize();
                    $.post("/ruleProduct/addRuleProduct", params, function(data) {
                        if (data.status == 200) {
                            $.messager.alert('提示', '新增成功!', 'info',
                                function() {
                                    $("#addRuleProductDialog").dialog('close');
                                    $("#rule-product-list").datagrid("reload");
                                });
                        }else if(data.status == 400){
                            $.messager.alert('警告',data.msg,'warning');
                        }
                    });
                }
            },{
                text:'关闭',
                handler:function(){$('#addRuleProductDialog').dialog("close");}
            }],
            onBeforeClose: function () {
                $("#addRuleProductDialog").form("clear");
            }
        });
    }

    deleteRuleProduct = function () {
        var ids = getRuleProductListSelectionsIds();
        if (ids.length == 0) {
            $.messager.alert('提示', '未选中数据!');
            return;
        }
        $.messager.confirm('确认', '确定删除ID为 ' + ids + ' 的数据吗？', function(r) {
            if (r) {
                var params = {"ids" : ids};
                $.post("/ruleProduct/deleteRuleProduct", params, function(data) {
                    if (data.status == 200) {
                        $.messager.alert('提示', '删除用户成功!', 'info',
                            function() {
                                $("#rule-product-list").datagrid("reload");
                            });
                    }
                });
            }
        });
    }

    userSearch = function () {
        $('#rule-product-list').datagrid('load');
    }

    getRuleProductListSelectionsIds = function() {
        var itemList = $("#rule-product-list");
        var sels = itemList.datagrid("getSelections");
        var ids = [];
        for ( var i in sels) {
            ids.push(sels[i].id);
        }
        ids = ids.join(",");
        return ids;
    }
</script>