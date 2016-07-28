package com.yunsoo.sqlite.service;

import com.yunsu.greendao.entity.Product;
import java.util.List;

/**
 * Created by yunsu on 2016/7/27.
 */
public interface ProductService {
    /*
     增加产品
     */
    public void addProduct(Product product);

    /*
    删除产品
    */
    public void removeProduct(Product product);

    /*
    修改产品信息
     */
    public void updateProduct(Product product);

    /*
    查询产品信息
     */
    public Product QueryProduct(Product product);

    /*
    根据包装ID查询所有的产品
     */
    public List<Product>  queryAllProductByPackId(Long packId);

    /*
    根据包装ID删除所有的产品
     */
    public void  removeAllProductByPackId(long packId);

}
