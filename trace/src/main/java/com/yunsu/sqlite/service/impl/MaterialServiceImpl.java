package com.yunsu.sqlite.service.impl;

import com.yunsu.greendao.dao.MaterialDao;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.MaterialService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaowu on 2016/8/22.
 */
public class MaterialServiceImpl implements MaterialService {
    private MaterialDao materialDao= GreenDaoManager.getInstance().getDaoSession().getMaterialDao();
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void insertMaterial(Material material) {
        material.setCreateTime(dateFormat.format(new Date()));
        materialDao.insert(material);
    }

    @Override
    public void updateMaterial(Material material) {
        materialDao.update(material);
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

        Material resultMaterial= queryById(material.getId());
        return resultMaterial.getPacks();
    }
}
