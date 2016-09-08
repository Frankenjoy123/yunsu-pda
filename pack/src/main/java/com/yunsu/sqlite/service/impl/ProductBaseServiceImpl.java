package com.yunsu.sqlite.service.impl;

import com.yunsu.greendao.dao.ProductBaseDao;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.ProductBaseService;

import java.util.List;

/**
 * Created by yunsu on 2016/9/8.
 */
public class ProductBaseServiceImpl implements ProductBaseService {

    ProductBaseDao productBaseDao= GreenDaoManager.getInstance().getDaoSession().getProductBaseDao();

    @Override
    public long insert(ProductBase productBase) {
        return productBaseDao.insert(productBase);
    }

    @Override
    public void delete(ProductBase productBase) {
        productBaseDao.delete(productBase);
    }

    @Override
    public void update(ProductBase productBase) {
        productBaseDao.update(productBase);
    }

    @Override
    public List<ProductBase> queryAllProductBase() {
        return productBaseDao.queryBuilder().list();
    }

    @Override
    public ProductBase queryProductBaseById(long id) {
        return productBaseDao.queryBuilder().
                where(ProductBaseDao.Properties.Id.eq(id)).unique();
    }
}
