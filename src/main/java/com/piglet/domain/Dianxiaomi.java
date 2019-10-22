package com.piglet.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

@TableName("dianxiaomi")
public class Dianxiaomi implements Serializable{
    private static final long serialVersionUID = 8750639181544596096L;
    @TableId
    private Integer dxmId;

    private String orderNo;

    private String expressNo;

    private String sku;

    private Integer count;

    private Double cost;

    private String expressMode;

    private Double price;

    private String country;

    private double orderAmount;

    public Integer getDxmId() {
        return dxmId;
    }

    public void setDxmId(Integer dxmId) {
        this.dxmId = dxmId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getExpressMode() {
        return expressMode;
    }

    public void setExpressMode(String expressMode) {
        this.expressMode = expressMode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }
}