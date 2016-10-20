package com.yunsu.sqlite.service;

import com.yunsu.entity.PackProductsEntity;
import com.yunsu.entity.StaffCountEntity;
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
    long addPack(Pack pack);

    /*
    删除包装
    */
    void removePack(Pack pack);

    /*
    修改包装信息
     */
     void updatePack(Pack pack);

    //更新日期中的所有包装的状态
    void updatePacksStatus(String date , String status);

    /*
    批量更新
     */
    void updateInTx(List<Pack> packList);

    /*
    根据包装码查询包装信息
     */
     Pack QueryPack(String packKey);


    /*
    查询所有的包装
     */
     List<Pack> queryAllPack();


    /*
    查询包装中的产品
     */
     List<Product> queryProducts(Pack pack);

    /*
    查询未同步的所有包装
     */

     List<Pack> queryNotSyncPacks();

    //根据日期查询员工的打包数和产品数
    List<StaffCountEntity>  queryPackProductCountByDate(String date);


    //查询未提交后台的日期列表
    List<String> queryNotCommitDateList();

    //根据日期查询status不是commit的所有的包装关系
    List<PackProductsEntity> queryPackProductsByDate(String date);


}
