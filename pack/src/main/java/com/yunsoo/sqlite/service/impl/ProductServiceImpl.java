package com.yunsu.sqlite.service.impl;

import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.ProductService;
import com.yunsu.greendao.dao.ProductDao;
import com.yunsu.greendao.entity.Product;

import java.util.List;

/**
 * Created by yunsu on 2016/7/28.
 */
public class ProductServiceImpl implements ProductService {
    private ProductDao productDao= GreenDaoManager.getInstance().getDaoSession().getProductDao();
    @Override
    public void addProduct(Product product) {
        productDao.insert(product);
    }

    @Override
    public void removeProduct(Product product) {

    }

    @Override
    public void updateProduct(Product product) {
        productDao.update(product);
    }

    @Override
    public Product QueryProduct(Product product) {
        return productDao.queryBuilder().
                where(ProductDao.Properties.ProductKey.eq(product.getProductKey()))
                .build().unique();
    }

    @Override
    public List<Product> queryAllProductByPackId(Long packId) {
       return  productDao._queryPack_Products(packId);
    }

    @Override
    public void removeAllProductByPackId(long packId) {
        List<Product> productList=queryAllProductByPackId(packId);
        for (Product product :productList){
            productDao.delete(product);
        }
    }
}
