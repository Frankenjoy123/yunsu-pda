package com.yunsoo.sqlite.service.impl;

import com.yunsoo.manager.GreenDaoManager;
import com.yunsoo.sqlite.service.MaterialService;
import com.yunsu.greendao.dao.MaterialDao;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Order;
import com.yunsu.greendao.entity.Pack;

import java.util.List;

/**
 * Created by xiaowu on 2016/8/16.
 */
public class MaterialServiceImpl implements MaterialService {
    private MaterialDao materialDao= GreenDaoManager.getInstance().getDaoSession().getMaterialDao();

    @Override
    public void insertMaterial(Material material) {
        materialDao.insert(material);
    }

    @Override
    public void updateMaterial(Material material) {
        materialDao.update(material);
    }

    @Override
    public Material queryByMaterialNumber(String materialNumber) {
        return  materialDao.queryBuilder().where(MaterialDao.Properties.MaterialNumber.eq(materialNumber)).unique();
    }

    @Override
    public Material queryById(long id) {
        return  materialDao.queryBuilder().where(MaterialDao.Properties.Id.eq(id)).unique();
    }

    @Override
    public void deleteMaterial(Material material) {
        materialDao.delete(material);
    }

    @Override
    public List<Pack> queryPacks(Material material) {
        Material resultMaterial=  materialDao.queryBuilder().
                where(MaterialDao.Properties.MaterialNumber.eq(material.getMaterialNumber())).unique();
        return resultMaterial.getPacks();
    }
}
