package com.piglet.service;

import com.piglet.domain.Freight;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface OrderService {

    void excel(InputStream is, String originalFilename, Integer userId);

    int resultCount();

    List<Map<String,Object>> resultList();

    void calculation(Integer userId);

    List<Freight> freightList();

    int chengbiaoCount(Map<String, Object> params);

    List<Map<String,Object>> chengbiaoList(Map<String, Object> params);

    int errorCount(Map<String, Object> params);

    List<Map<String, Object>> errorList(Map<String, Object> params);

    int saveRate(Map<String, Object> params);

    List<Map<String, Object>> chengbiaoRecords();
}
