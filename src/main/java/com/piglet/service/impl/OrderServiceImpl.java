package com.piglet.service.impl;

import com.piglet.domain.Dianxiaomi;
import com.piglet.domain.Freight;
import com.piglet.domain.Order;
import com.piglet.domain.ResultLog;
import com.piglet.mapper.LogMapper;
import com.piglet.mapper.OrderMapper;
import com.piglet.service.OrderService;
import com.piglet.util.Excel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Service
@Transactional(propagation= Propagation.NESTED,isolation= Isolation.DEFAULT,readOnly = false,rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    LogMapper logMapper;

    @Transactional(readOnly = false,rollbackFor = Exception.class)
    @Override
    public void excel(InputStream is, String originalFilename , Integer userId) {
        try {
            List<ArrayList<Object>> list;
            if (originalFilename.endsWith(".xls")) {
                list = Excel.readExcel2003(is);
            } else {
                list = Excel.readExcel2007(is);
            }
            if (originalFilename.startsWith("order")) {
                int j = list.size();
                orderMapper.removeAll();
                for (int i = 0; i < j; i++) {
                    List<Object> row = list.get(i);
                    if (row.get(0).toString() != null && !"".equals(row.get(0).toString())) {
                        Order order = new Order();
                        order.setSku(row.get(0).toString());
                        order.setAmount(Double.parseDouble(row.get(1).toString()));
                        order.setProfit(Double.parseDouble(row.get(2).toString()));
                        orderMapper.insert(order);
                    }
                }
                List<Map<String, Object>> resultList = orderMapper.monthResult("");
                String content = "";
                for (Map<String, Object> map : resultList) {
                    content += map.get("realname") + "：{一月内：（金额：" + map.get("onemonth_amount") + "元，利润：" + map.get("onemonth_profit") + "美元），" +
                            "一月到三月：（金额：" + map.get("onethreemonth_amount") + "元，利润：" + map.get("onethreemonth_profit") + "美元），" +
                            "三月到六月：（金额：" + map.get("threesixmonth_amount") + "元，利润：" + map.get("threesixmonth_profit") + "美元），" +
                            "六月到12月：（金额：" + map.get("halfyearmonth_amount") + "元，利润：" + map.get("halfyearmonth_profit") + "美元），" +
                            "12月前：（金额：" + map.get("yearmonth_amount") + "元，利润：" + map.get("yearmonth_profit") + "美元），" +
                            "总计：（金额：" + map.get("sum_amount") + "元，利润：" + map.get("sum_profit") + "美元）}";
                }
                ResultLog resultLog = new ResultLog();
                resultLog.setFileName(originalFilename);
                resultLog.setContent(content);
                resultLog.setCreateTime(new Date());
                resultLog.setCreatePerson(userId);
                resultLog.setDelFlag(0);
                logMapper.insert(resultLog);
            } else if (originalFilename.startsWith("dianxiaomi")) {
                orderMapper.truncateAll();
                int j = list.size();
                for (int i = 0; i < j; i++) {
                    List<Object> row = list.get(i);
                    if(row.get(0).toString()!=null && !"".equals(row.get(0).toString())) {
                        Dianxiaomi dxm = new Dianxiaomi();
                        dxm.setOrderNo(row.get(0).toString());
                        if (row.get(1).toString() != null && !"".equals(row.get(1).toString()))
                            dxm.setCost(Double.parseDouble((row.get(1).toString()).trim()));
                        if (row.get(2).toString() != null && !"".equals(row.get(2).toString()))
                            dxm.setOrderAmount(Double.parseDouble(row.get(2).toString()));
                        dxm.setSku(row.get(3).toString());
                        dxm.setPrice(Double.parseDouble(row.get(4).toString()));
                        dxm.setCount((int) Double.parseDouble(row.get(5).toString()));
                        dxm.setCountry(row.get(6).toString());
                        dxm.setExpressMode(row.get(7).toString());
                        dxm.setExpressNo(row.get(8).toString());
                        orderMapper.saveDianxiaomi(dxm);
                    }
                }
                //判断多订单多运单
                List<Map<String, Object>> orderAndexpress = orderMapper.mome();
                if(!orderAndexpress.isEmpty() && orderAndexpress.size() != 0 && orderAndexpress != null){
                    for(Map<String, Object> ox:orderAndexpress){
                        String orderNo = (String)ox.get("order_no");
                        String expressNo = (String)ox.get("express_no");
                        if(orderNo != null && !"".equals(orderNo)){
                            orderMapper.insertDEByOrderNo(orderNo,"多订单多运单");//通过订单号将错误数据插入店小秘错误视图表
                            orderMapper.deleteDXMByOrderNo(orderNo);//通过订单号删除店小秘中的记录
                        }
                        if(expressNo != null && !"".equals(expressNo)){
                            orderMapper.insertDEByExpressNo(expressNo,"多订单多运单");//通过运单号将错误数据插入店小秘错误视图表
                            orderMapper.deleteDXMByExpressNo(expressNo);//通过运单号删除店小秘中的记录
                        }
                    }
                }
                //判断订单金额为0
                List<String> zeroOrder = orderMapper.zeroOrder();
                if(!zeroOrder.isEmpty() && zeroOrder.size() != 0 && zeroOrder != null){
                    for (String orderNo : zeroOrder){
                        orderMapper.insertDEByOrderNo(orderNo,"订单金额为0");//通过订单号将错误数据插入店小秘错误视图表
                        orderMapper.deleteDXMByOrderNo(orderNo);//通过订单号删除店小秘中的记录
                    }
                }
                //插入单订单多运单(oome)、多订单单运单(mooe)、单订单单运单(oooe)分表
                orderMapper.insertPartTable();
                //判断成本是否为空
                List<String> oomeCost = orderMapper.oomeCost();  //取出值为订单号
                List<String> mooeCost = orderMapper.mooeCost();  //取出值为运单号
                List<String> oooeCost = orderMapper.oooeCost();  //取出值为订单号
                if(!oomeCost.isEmpty() && oomeCost.size() != 0 && oomeCost != null){
                    for (String orderNo : oomeCost){
                        orderMapper.insertDEByOrderNo(orderNo,"订单成本为0");//通过订单号将错误数据插入店小秘错误视图表
                        orderMapper.deleteDXMByOrderNo(orderNo);//通过订单号删除店小秘中的记录
                    }
                }
                if(!mooeCost.isEmpty() && mooeCost.size() != 0 && mooeCost != null){
                    for (String expressNo : mooeCost){
                        orderMapper.insertDEByExpressNo(expressNo,"运单成本为0");//通过运单号将错误数据插入店小秘错误视图表
                        orderMapper.deleteDXMByExpressNo(expressNo);//通过运单号删除店小秘中的记录
                    }
                }
                if(!oooeCost.isEmpty() && oooeCost.size() != 0 && oooeCost != null){
                    for (String orderNo : oooeCost){
                        orderMapper.insertDEByOrderNo(orderNo,"订单成本为0");//通过订单号将错误数据插入店小秘错误视图表
                        orderMapper.deleteDXMByOrderNo(orderNo);//通过订单号删除店小秘中的记录
                    }
                }
                if((!oomeCost.isEmpty() && oomeCost.size() != 0 && oomeCost != null)||(!mooeCost.isEmpty() && mooeCost.size() != 0 && mooeCost != null)||(!oooeCost.isEmpty() && oooeCost.size() != 0 && oooeCost != null)){
                    orderMapper.truncatePartTable();
                    orderMapper.insertPartTable();
                }

            } else if (originalFilename.startsWith("freight")) {
                orderMapper.removeFreight();
                int j = list.size();
                for (int i = 0; i < j; i++) {
                    List<Object> row = list.get(i);
                    if (row.get(0).toString() != null && !"".equals(row.get(0).toString())) {
                        Freight freight = new Freight();
                        freight.setExpressNo(row.get(0).toString());
                        freight.setAmount(Double.parseDouble(row.get(1).toString()));
                        orderMapper.saveFreight(freight);
                    }
                }
                //判断是否有运费不匹配记录
                List<String> noFreight = orderMapper.noFreight();
                if(!noFreight.isEmpty() && noFreight.size() != 0 && noFreight != null){
                    for (String expressNo : noFreight){
                        orderMapper.insertDEByExpressNo(expressNo,"运单号不匹配");//通过运单号将错误数据插入店小秘错误视图表
                        orderMapper.deleteDXMByExpressNo(expressNo);//通过运单号删除店小秘中的记录
                    }
                    orderMapper.truncatePartTable();
                    orderMapper.insertPartTable();
                }
            } else if (originalFilename.startsWith("amount")) {
                orderMapper.removeOrderAmount();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int resultCount() {
        return orderMapper.resultCount();
    }

    @Override
    public List<Map<String, Object>> resultList() {
        return orderMapper.monthResult("");
    }

    @Transactional(readOnly = false,rollbackFor = Exception.class)
    @Override
    public void calculation(Integer userId) {
        try{
            orderMapper.insertFreightSum();//插入运单合计表(因freight表存在重复未合并情况)
            orderMapper.insertOrderFreight();//插入订单运费表(拆包)拆包情况
            orderMapper.insertOrderAmount();//插入订单金额表
            orderMapper.insertExpressCost();//插入运单成本表
            orderMapper.insertOrderCost();//插入订单成本表
            orderMapper.insertExpressPrice();//插入运单总售价表
            orderMapper.insertOrderPrice();//插入订单总售价表
            orderMapper.mooeCalculation();//计算单条记录
            orderMapper.oomeCalculation();//计算单条记录
            orderMapper.oooeCalculation();//计算单条记录
            orderMapper.profitCalculation();//计算利润，小数点第三位向下取整
            orderMapper.insertBakOrder();//将有用信息插入一张表中
            orderMapper.insertOrder();
            orderMapper.monthResult("");
            List<Map<String, Object>> resultList = orderMapper.monthResult("");
            String content = "";
            for (Map<String, Object> map : resultList) {
                content += map.get("realname") + "：{一月内：（金额：" + map.get("onemonth_amount") + "元，利润：" + map.get("onemonth_profit") + "美元），" +
                        "一月到三月：（金额：" + map.get("onethreemonth_amount") + "元，利润：" + map.get("onethreemonth_profit") + "美元），" +
                        "三月到六月：（金额：" + map.get("threesixmonth_amount") + "元，利润：" + map.get("threesixmonth_profit") + "美元），" +
                        "六月到12月：（金额：" + map.get("halfyearmonth_amount") + "元，利润：" + map.get("halfyearmonth_profit") + "美元），" +
                        "12月前：（金额：" + map.get("yearmonth_amount") + "元，利润：" + map.get("yearmonth_profit") + "美元），" +
                        "总计：（金额：" + map.get("sum_amount") + "元，利润：" + map.get("sum_profit") + "美元）}";
            }
            ResultLog resultLog = new ResultLog();
            resultLog.setContent(content);
            resultLog.setCreateTime(new Date());
            resultLog.setCreatePerson(userId);
            resultLog.setDelFlag(0);
            logMapper.insert(resultLog);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Freight> freightList() {
        return orderMapper.freightList();
    }

    @Override
    public int chengbiaoCount(Map<String, Object> params) {
        return orderMapper.chengbiaoCount(params);
    }

    @Override
    public List<Map<String, Object>> chengbiaoList(Map<String, Object> params) {
        if((String)params.get("page")!=null && (String)params.get("page")!=""
                &&(String)params.get("limit")!=null && (String)params.get("limit")!="") {
            int page = Integer.parseInt((String) params.get("page"));
            int limit = Integer.parseInt((String) params.get("limit"));
            params.put("offset", (page - 1 )* limit);
        }
        return orderMapper.chengbiaoList(params);
    }

    @Override
    public int errorCount(Map<String, Object> params) {
        return orderMapper.errorCount(params);
    }

    @Override
    public List<Map<String, Object>> errorList(Map<String, Object> params) {
        if((String)params.get("page")!=null && (String)params.get("page")!=""
                &&(String)params.get("limit")!=null && (String)params.get("limit")!="") {
            int page = Integer.parseInt((String) params.get("page"));
            int limit = Integer.parseInt((String) params.get("limit"));
            params.put("offset", (page - 1 )* limit);
        }
        return orderMapper.errorList(params);
    }

    @Override
    public int saveRate(Map<String, Object> params) {
        String rate = (String)params.get("exchangeRate");
        if(rate != null && !"".equals(rate)){
            params.put("exchangeRate",Double.parseDouble(rate));
        }
        return orderMapper.saveRate(params);
    }

    @Override
    public List<Map<String, Object>> chengbiaoRecords() {
        return orderMapper.chengbiaoRecords();
    }

    @Override
    public List<Map<String, Object>> resultListByUser(String userflag) {
        return orderMapper.monthResult(userflag);
    }
}
