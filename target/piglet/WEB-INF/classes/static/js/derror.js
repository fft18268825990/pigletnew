layui.use('table', function() {
    var table = layui.table;
    table.render({
        elem: '#errorList'
        , url: '/errorList' //数据接口
        , page: false //开启分页
        , toolbar: '#toolbar'
        , cols: [[ //表头
            {field: 'dxm_id', width:'9%',title: 'id'}
            , {field: 'order_no', width:'9%',title: '订单号'}
            , {field: 'express_no', width:'9%',title: '运单号'}
            , {field: 'cost', width:'9%',title: '成本'}
            , {field: 'express_mode', width:'9%',title: '物流方式'}
            , {field: 'price', width:'9%',title: '售价'}
            , {field: 'sku', width:'9%',title: 'SKU'}
            , {field: 'count', width:'9%',title: '数量'}
            , {field: 'country', width:'9%',title: '国家'}
            , {field: 'order_amont', width:'9%',title: '订单金额'}
            , {field: 'remark', width:'9%',title: '错误信息'}
        ]]
    });
});
