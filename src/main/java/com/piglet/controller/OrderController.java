package com.piglet.controller;

import com.piglet.domain.Freight;
import com.piglet.service.OrderService;
import com.piglet.util.PageUtil;
import com.piglet.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.piglet.util.ShiroUtils.getUserId;

@Controller
public class OrderController {
    @Autowired
    OrderService orderService;

    @RequestMapping("/excelResult")
    public String excelResult(){
        return "excel-result";
    }

    @RequestMapping("/multipleExcel")
    public String multipleExcel(){
        return "multiple-excel";
    }

    @RequestMapping("/dError")
    public String dError(){
        return "dianxiaomi-error";
    }


    @RequestMapping(value = "/excel")
    @ResponseBody
    public Result InputExcel(@RequestParam("file") MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();// 原文件名字
                InputStream is = file.getInputStream();// 获取输入流
                orderService.excel(is,originalFilename,getUserId());
            } catch (Exception e) {
                return Result.error();
            }
            return Result.ok();
        }else return Result.error();
    }

    @RequestMapping(value = "/calculation")
    @ResponseBody
    public Result calculation() throws Exception {
        try {
            List<Freight> freightList = orderService.freightList();
            if(freightList == null || freightList.size() == 0 || freightList.isEmpty()){
                return Result.error("请先导入运单表");
            }
            orderService.calculation(getUserId());
        } catch (Exception e) {
            return Result.error();
        }
        return Result.ok();
    }


    @GetMapping(value = "/resultList")
    @ResponseBody
    public PageUtil resultList(@RequestParam Map<String, Object> params) throws Exception {
        String userflag = (String)params.get("userflag");
        if(userflag == "" || userflag == null){
            return new PageUtil("0","返回成功",orderService.resultCount(),orderService.resultList());
        }else{
            List<Map<String, Object>> list = orderService.resultListByUser(userflag);
            return new PageUtil("0","返回成功",list.size(),list);
        }
    }

    @GetMapping(value = "/chengbiao")
    @ResponseBody
    public PageUtil chengbiao(@RequestParam Map<String, Object> params) throws Exception {
        return new PageUtil("0","返回成功",orderService.chengbiaoCount(params),orderService.chengbiaoList(params));
    }

    @GetMapping(value = "/chengbiaoList")
    @ResponseBody
    public List<Map<String,Object>> chengbiaoList(@RequestParam Map<String, Object> params) {
        return orderService.chengbiaoRecords();
    }

    @GetMapping(value = "/errorList")
    @ResponseBody
    public PageUtil errorList(@RequestParam Map<String, Object> params) throws Exception {
        return new PageUtil("0","返回成功",orderService.errorCount(params),orderService.errorList(params));
    }

    @PostMapping(value = "/saveRate")
    @ResponseBody
    public Result saveRate(@RequestParam Map<String, Object> params) throws Exception {
        if (orderService.saveRate(params) > 0) {
            return Result.ok();
        }
        return Result.error();
    }
}
