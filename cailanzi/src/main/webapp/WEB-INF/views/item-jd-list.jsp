<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="width: 100%;height: 40px;">
    到家商品编码:&nbsp;&nbsp;<input class="easyui-textbox" id="jd_list_skuId"/>&nbsp;&nbsp;
    商品名称:&nbsp;&nbsp;<input class="easyui-textbox" id="jd_list_skuName"/>&nbsp;&nbsp;
    商家商品状态:&nbsp;&nbsp;<input id="jd_list_fixedStatusCombo"/>
                    <input type="hidden" id="jd_list_fixedStatus"/>

    <button style="margin-left: 10px" class="easyui-linkbutton" iconCls="icon-search" onclick="itemJdListSearch()">搜索</button>
</div>
<table id="item-jd-list" style="width:100%;height:800px"></table>

<script type="text/javascript" charset="utf-8">

    $('#jd_list_fixedStatusCombo').combobox({
        valueField:'key',
        textField:'value',
        data: [{
            key: '',
            value: '全部'
        },{
            key: '1',
            value: '上架'
        },{
            key: '2',
            value: '下架'
        },{
            key: '4',
            value: '删除'
        }],
        onSelect: function (record) {
            $("#jd_list_fixedStatus").val(record.key);
        }
    });

    $('#item-jd-list').datagrid({
        url:'/product/jd/productPage',
        title: '商品列表',
        pagePosition: 'top',
        rownumbers: true,
        singleSelect: false ,
        collapsible:true,
        pagination:true,
        pageSize: 20,
        pageList:[20,50,100],
        toolbar: [{
            text : '同步',
            iconCls : 'icon-add',
            handler : function() {
                $.messager.confirm('确认对话框', '您确认要同步吗？', function(r){
                    if (r){
                        $.messager.progress();
                        $.post("/product/jd/asynProduct", null, function(data) {
                            if (data.status == 200) {
                                $.messager.alert('提示', '商品同步成功!', 'info');
                                $.messager.progress('close');
                                $('#item-jd-list').datagrid('reload');
                            }
                        });
                    }
                });
            }
        }],
        columns:[[
            {field:'id',checkbox:true},
            {field:'skuId',title:'到家商品编码',width:90,align:'center'},
            /*{field:'outSkuId',title:'商家商品编码',width:100,align:'center'},*/
            {field:'skuName',title:'商品名称',width:450,align:'center'},
            {field:'skuPrice',title:'商品价格',width:70,align:'center'},
            {field:'stockNum',title:'库存数量',width:100,align:'center'},
            {field:'upcCode',title:'商品UPC编码',width:100,align:'center'},
            {field:'categoryId',title:'到家类目编码',width:90,align:'center'},
            {field:'shopCategories',title:'商家分类编码',width:90,align:'center'},
            {field:'orgCode',title:'商户编码',width:60,align:'center'},
            {field:'sellCities',title:'销售城市',width:60,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '全国';
                    } else {
                        return value;
                    }
                }
            },
            {field:'fixedStatus',title:'商家商品状态',width:100,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '上架';
                    } else if(value==2){
                        return '<span style="color:red;">下架</span>';
                    } else if(value==4){
                        return '删除'
                    }
                }
            },
            {field:'fixedUpTime',title:'上线时间',width:150,align:'center'},
            {field:'fixedDownTime',title:'下架时间',width:150,align:'center'},
            {field:'syncTime',title:'同步时间',width:150,align:'center'},
        ]],
        onBeforeLoad: function (param) {
            param.pageNo = param.page;
            param.pageSize = param.rows;
            param.skuId = $("#jd_list_skuId").val();
            param.fixedStatus = $("#jd_list_fixedStatus").val();
            param.skuName = $("#jd_list_skuName").val();
            return true;
        }
    });

    itemJdListSearch = function () {
        $('#item-jd-list').datagrid('load');
    }

</script>
