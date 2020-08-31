package com.piglet.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@TableName("product")
public class Product implements Serializable {
    private static final long serialVersionUID = 8750639181544596096L;
    @TableId
    private Integer productId;
    private String sku;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Integer createPerson;
    private Integer paipin;
    private Integer meigong;
    private Integer kaifa;
    private Integer shangchuan;
    private Integer yunying;
    private Integer delFlag;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(Integer createPerson) {
        this.createPerson = createPerson;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getPaipin() {
        return paipin;
    }

    public void setPaipin(Integer paipin) {
        this.paipin = paipin;
    }

    public Integer getMeigong() {
        return meigong;
    }

    public void setMeigong(Integer meigong) {
        this.meigong = meigong;
    }

    public Integer getKaifa() {
        return kaifa;
    }

    public void setKaifa(Integer kaifa) {
        this.kaifa = kaifa;
    }

    public Integer getShangchuan() {
        return shangchuan;
    }

    public void setShangchuan(Integer shangchuan) {
        this.shangchuan = shangchuan;
    }

    public Integer getYunying() {
        return yunying;
    }

    public void setYunying(Integer yunying) {
        this.yunying = yunying;
    }
}