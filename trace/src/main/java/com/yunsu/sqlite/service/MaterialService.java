package com.yunsu.sqlite.service;

import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Pack;

import java.util.List;

/**
 * Created by yunsu on 2016/8/8.
 */
public interface MaterialService {
    //插入物料
    long insertMaterial(Material material);

    //更新物料
    void updateMaterial(Material material);

    //根据ID查询物流
    Material queryById(long id);

    //查询全部的物料订单
    List<Material> queryAllMaterial();

    List<Material> queryMaterialByPage(int offSet);

    //删除物料
    void deleteMaterial(Material material);

    //查询物流中的所有包装
    List<Pack> queryPacks(Material material);

}
