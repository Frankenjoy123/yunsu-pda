package com.yunsu.entity;

import com.yunsu.greendao.entity.Pack;

/**
 * Created by xiaowu on 2016/10/18.
 */
public class PackProductsEntity {
    private Pack pack;

    private String productsString;

    public Pack getPack() {
        return pack;
    }

    public void setPack(Pack pack) {
        this.pack = pack;
    }

    public String getProductsString() {
        return productsString;
    }

    public void setProductsString(String productsString) {
        this.productsString = productsString;
    }
}
