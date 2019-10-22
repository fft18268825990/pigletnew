var exportDatas;
var table;
var layer;
var upload;
layui.use(['upload','table','layer'], function() {
     upload = layui.upload;
     layer = layui.layer;
     table = layui.table;
    var demoListView = $('#demoList')
        , uploadListIns = upload.render({
        elem: '#testList'
        , url: '/excel'
        , accept: 'file'
        , multiple: true
        , auto: false
        , bindAction: '#testListAction'
        , choose: function (obj) {
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                var tr = $(['<tr id="upload-' + index + '">'
                    , '<td>' + file.name + '</td>'
                    , '<td>' + (file.size / 1024).toFixed(1) + 'kb</td>'
                    , '<td>等待上传</td>'
                    , '<td>'
                    , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                    , '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                    , '</td>'
                    , '</tr>'].join(''));

                //单个重传
                tr.find('.demo-reload').on('click', function () {
                    obj.upload(index, file);
                });

                //删除
                tr.find('.demo-delete').on('click', function () {
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });

                demoListView.append(tr);
            });
        }
        , done: function (res, index, upload) {
            if (res.code == 0) { //上传成功
                var tr = demoListView.find('tr#upload-' + index)
                    , tds = tr.children();
                tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                tds.eq(3).html(''); //清空操作
                return delete this.files[index]; //删除文件队列已经上传成功的文件
            }
            this.error(index, upload);
        }
        , error: function (index, upload) {
            var tr = demoListView.find('tr#upload-' + index)
                , tds = tr.children();
            tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
    });
    loadTable()
});

function loadTable(){
    table.render({
        elem: '#chengbiao'
        , url: '/chengbiao' //数据接口
        , page: true //开启分页
        , toolbar: '#toolbar'
        , cols: [[ //表头
            {type: 'numbers'}
            , {field: 'order_no', width:'10%',title: '订单号'}
            , {field: 'sku', width:'9%',title: 'SKU'}
            , {field: 'cost', width:'9%',title: '成本'}
            , {field: 'express_cost', width:'9%',title: '运费'}
            , {field: 'order_amount', width:'9%',title: '订单金额'}
            , {field: 'actual_amount', width:'9%',title: '实际可得'}
            , {field: 'profit', width:'9%',title: '利润'}
            , {field: 'express_no', width:'10%',title: '运单号'}
            , {field: 'express_mode', width:'15%',title: '物流方式'}
            , {field: 'country', width:'9%',title: '国家'}
        ]]
    });
    table.on('toolbar(chengbiao)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id); //获取选中行状态
        switch(obj.event){
            case 'export':
                chengbiaoList();
                table.exportFile(['运单号','物流方式','国家', '运费','成本','订单金额','实际可得','id','运单号','SKU','利润'], exportDatas, 'xls');
                break;
        };
    });
}

function calculation(){
    $.ajax({
        type: "GET",
        url: "/calculation",
        success: function (r) {
            console.log(r);
            if (r.code == 0) {
                layer.msg("计算完成");
                loadTable();
            }else{
                layer.msg(r.msg);
            }
        }
    });
}

function saveRate(){
    $.ajax({
        type: "POST",
        url: "/saveRate",
        data:{'exchangeRate': $('#exchangeRate').val()},
        success: function (r) {
            if (r.code == 0) {
                    layer.alert("汇率保存成功", {
                            icon: 6
                        });

            }else{
                layer.msg(r.msg);
            }
        }
    });
}

function chengbiaoList(){
    $.ajax({
        type : "Get",
        url : "/chengbiaoList",
        async : false,
        success : function(data) {
            exportDatas = data;
        }
    });
}