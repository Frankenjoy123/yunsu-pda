package com.yunsu.sqlite.service.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    private SQLiteDatabase db=GreenDaoManager.getInstance().getDb();

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

    @Override
    public boolean existPackDataByProductBaseId(long id) {
        StringBuilder builder=new StringBuilder();
        builder.append("select count(*)");
        builder.append("from Pack p inner join product_base pb on p.product_base_id = pb._id ");
        builder.append("where p.product_base_id=?");

        Cursor c=db.rawQuery(builder.toString(),new String[]{String.valueOf(id)});

        if (c!=null){
            c.moveToFirst();
            int count = c.getInt(0);
            if (count>0){
                return true;
            }
        }
        return false;
    }
}
