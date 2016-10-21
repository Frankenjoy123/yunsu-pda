package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.ProductBase;

import java.util.List;

/**
 * Created by yunsu on 2016/9/8.
 */
public interface ProductBaseService {

    long insert(ProductBase productBase);

    void delete(ProductBase productBase);

    void update(ProductBase productBase);

    List<ProductBase> queryAllProductBase();

    ProductBase queryProductBaseById(long id);

    boolean existPackDataByProductBaseId(long id);

}
