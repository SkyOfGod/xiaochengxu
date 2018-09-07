<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table id="shop-jd-list" style="width:100%;height:800px"></table>

<script type="text/javascript">
    $('#shop-jd-list').datagrid({
        url:'/shop/list',
        title: '商户列表',
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
                        $.post("/shop/syncShop", null, function(data) {
                            if (data.status == 200) {
                                $.messager.alert('提示', '商户同步成功!', 'info');
                                $.messager.progress('close');
                                $('#shop-jd-list').datagrid('reload');
                            }
                        });
                    }
                });
            }
        }],
        columns:[[
            {field:'id',checkbox:true},
            {field:'venderId',title:'商家编码',width:60,align:'center'},
            {field:'venderName',title:'商家名称',width:120,align:'center'},
            {field:'stationName',title:'门店名称',width:180,align:'center'},
            {field:'stationNo',title:'到家门店编码',width:90,align:'center'},
            {field:'cityName',title:'城市名称',width:60,align:'center'},
            {field:'countyName',title:'镇名称',width:60,align:'center'},
            {field:'phone',title:'门店电话',width:100,align:'center'},
            {field:'mobile',title:'门店手机',width:60,align:'center'},
            {field:'stationAddress',title:'地址',width:400,align:'center'},
            {field:'standByPhone',title:'备联电话',width:100,align:'center'},
            {field:'orderAging',title:'订单时效',width:60,align:'center'},
            {field:'yn',title:'门店状态',width:60,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '启用';
                    } else if(value==1) {
                        return '禁用';
                    } else {
                        return value;
                    }
                }
            },
            {field:'closeStatus',title:'营业状态',width:60,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '正常营业';
                    } else if(value==1){
                        return '<span style="color:red;">休息中</span>';
                    } else {
                        return value;
                    }
                }
            },
            {field:'preWarehouse',title:'是否前置仓',width:75,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '是';
                    } else if(value==2) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'selfPickSupport',title:'是否支持自提',width:90,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '支持';
                    } else {
                        return '不支持';
                    }
                }
            },
            {field:'wareType',title:'是否是仓库类型门店',width:135,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '是';
                    } else if(value==2) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'stationDeliveryStatus',title:'是否已设置配送服务',width:135,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '是';
                    } else if(value==2) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'supportOfflinePurchase',title:'是否支持线下购',width:90,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '不支持';
                    } else if(value==1) {
                        return '支持';
                    } else {
                        return value;
                    }
                }
            },
            {field:'timeAmType',title:'是否支持上午配送',width:120,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '支持';
                    } else {
                        return '不支持';
                    }
                }
            },
            {field:'timePmType',title:'是否支持下午配送',width:120,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '支持';
                    } else {
                        return '不支持';
                    }
                }
            },
            {field:'isMembership',title:'是否会员店',width:75,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '是';
                    } else {
                        return '否';
                    }
                }
            },
            {field:'innerNoStatus',title:'中间号状态',width:75,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '开启';
                    } else if(value==0) {
                        return '关闭';
                    } else {
                        return value;
                    }
                }
            },
            {field:'isAutoOrder',title:'是否自动接单',width:90,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '是';
                    } else if(value==1) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'isNoPaper',title:'是否无纸化',width:75,align:'center',
                formatter: function(value,row,index){
                    if (value==1){
                        return '是';
                    } else if(value==2) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'isAutoOrder',title:'是否自动接单',width:90,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '是1';
                    } else if(value==1) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'supportInvoice',title:'是否支持发票',width:90,align:'center',
                formatter: function(value,row,index){
                    if (value==0){
                        return '是';
                    } else if(value==1) {
                        return '否';
                    } else {
                        return value;
                    }
                }
            },
            {field:'createPin',title:'创建人',width:100,align:'center'},
            {field:'createTime',title:'创建时间',width:150,align:'center'},
            {field:'updatePin',title:'更新操作人',width:100,align:'center'},
            {field:'updateTime',title:'更新时间',width:150,align:'center'},
            {field:'syncTime',title:'同步时间',width:150,align:'center'},
        ]]
    });
</script>