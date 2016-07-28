package com.yunsoo.sqlite.service.impl;

import com.yunsoo.manager.GreenDaoManager;
import com.yunsoo.sqlite.service.PackService;
import com.yunsoo.util.Constants;
import com.yunsu.greendao.dao.DaoSession;
import com.yunsu.greendao.dao.PackDao;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;

import java.util.List;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by yunsu on 2016/7/27.
 */
public class PackServiceImpl implements PackService{
    private PackDao packDao=GreenDaoManager.getInstance().getDaoSession().getPackDao();
    @Override
    public void addPack(Pack pack) {
        packDao.insert(pack);
    }

    @Override
    public void removePack(Pack pack) {
        packDao.delete(pack);
    }

    @Override
    public void updatePack(Pack pack) {
        packDao.update(pack);
    }

    @Override
    public Pack QueryPack(String packKey) {
        Query query= packDao.queryBuilder().
                where(PackDao.Properties.PackKey.eq(packKey)).build();
        return (Pack) query.unique();
    }


    @Override
    public List<Pack> queryAllPack() {
        return packDao.queryBuilder().list();
    }

    @Override
    public List<Product> queryProducts(Pack pack) {

        Query query=packDao.queryBuilder().
                where(PackDao.Properties.PackKey.eq(pack.getPackKey())).build();
        Pack resultPack= (Pack) query.unique();
        return resultPack.getProducts();
    }

    @Override
    public List<Pack> queryNotSyncPacks() {
        return  packDao.queryBuilder().where(PackDao.Properties.Status.eq(Constants.DB.NOT_SYNC)).list();
    }
}
