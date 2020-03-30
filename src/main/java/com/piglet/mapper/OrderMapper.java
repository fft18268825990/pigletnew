package com.piglet.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.piglet.domain.Dianxiaomi;
import com.piglet.domain.Freight;
import com.piglet.domain.Order;
import com.piglet.domain.ResultLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    void removeAll();

    List<Map<String,Object>> monthResult(@Param(value="userflag")String userflag);

    List<Map<String,Object>> monthResultList();

    void saveLog(ResultLog resultLog);

    int resultCount();

    void removeDianxiaomi();

    void removeFreight();

    void removeOrderAmount();

    void saveDianxiaomi(Dianxiaomi dxm);

    void saveFreight(Freight freight);

    void truncateAll();

    List<Map<String, Object>> mome();

    void insertDEByOrderNo(@Param("orderNo") String orderNo, @Param("remark") String remark);

    void deleteDXMByOrderNo(String orderNo);

    void insertDEByExpressNo(@Param("expressNo") String expressNo, @Param("remark") String remark);

    void deleteDXMByExpressNo(String expressNo);

    List<String> zeroOrder();

    void insertPartTable();

    List<String> oomeCost();

    List<String> mooeCost();

    List<String> oooeCost();

    List<String> noFreight();

    void insertFreightSum();

    void insertOrderFreight();

    void insertOrderAmount();

    void insertExpressCost();

    void insertOrderCost();

    void insertExpressPrice();

    void insertOrderPrice();

    void mooeCalculation();

    void oomeCalculation();

    void oooeCalculation();

    void profitCalculation();

    List<Freight> freightList();

    void truncatePartTable();

    void insertBakOrder();

    void insertOrder();

    int chengbiaoCount(Map<String, Object> params);

    List<Map<String, Object>> chengbiaoList(Map<String, Object> params);

    int errorCount(Map<String, Object> params);

    List<Map<String,Object>> errorList(Map<String, Object> params);

    int saveRate(Map<String, Object> params);

    List<Map<String, Object>> chengbiaoRecords();
}
