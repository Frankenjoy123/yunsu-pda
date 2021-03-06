package com.yunsu.greendao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "EXPRESS".
 */
public class Express {

    private Long id;
    private String packKey;
    private String expressKey;
    private String createTime;

    public Express() {
    }

    public Express(Long id) {
        this.id = id;
    }

    public Express(Long id, String packKey, String expressKey, String createTime) {
        this.id = id;
        this.packKey = packKey;
        this.expressKey = expressKey;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackKey() {
        return packKey;
    }

    public void setPackKey(String packKey) {
        this.packKey = packKey;
    }

    public String getExpressKey() {
        return expressKey;
    }

    public void setExpressKey(String expressKey) {
        this.expressKey = expressKey;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
