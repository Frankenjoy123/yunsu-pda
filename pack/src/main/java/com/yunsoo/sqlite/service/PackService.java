package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;

import java.util.List;

/**
 * Created by yunsu on 2016/7/27.
 */
public interface PackService {
    /*
     增加包装
     */
    public void addPack(Pack pack);

    /*
    删除包装
    */
    public void removePack(Pack pack);

    /*
    修改包装信息
     */
    public void updatePack(Pack pack);

    /*
    根据包装码查询包装信息
     */
    public Pack QueryPack(String packKey);


    /*
    查询所有的包装
     */
    public List<Pack> queryAllPack();


    /*
    查询包装中的产品
     */
    public List<Product> queryProducts(Pack pack);

    /*
    查询未同步的所有包装
     */

    public List<Pack> queryNotSyncPacks();


}
