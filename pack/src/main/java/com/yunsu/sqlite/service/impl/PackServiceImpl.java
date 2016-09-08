package com.yunsu.sqlite.service.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunsu.common.util.Constants;
import com.yunsu.entity.StaffCountEntity;
import com.yunsu.greendao.dao.PackDao;
import com.yunsu.greendao.dao.StaffDao;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.PackService;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by xiaowu on 2016/7/27.
 */
public class PackServiceImpl implements PackService{
    private PackDao packDao=GreenDaoManager.getInstance().getDaoSession().getPackDao();
    private StaffDao staffDao=GreenDaoManager.getInstance().getDaoSession().getStaffDao();

    private SQLiteDatabase db=GreenDaoManager.getInstance().getDb();
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

    @Override
    public List<StaffCountEntity> queryPackProductCountByDate(String date) {

        Cursor c = db.rawQuery("select staff_id, count(*) , sum(real_count)  from Pack where date(last_save_time)=? group by staff_id",
                new String[]{date});
        List<StaffCountEntity> staffCountEntityList=new ArrayList<>();

        if (c!=null){
            while(c.moveToNext()) {
                StaffCountEntity staffCountEntity=new StaffCountEntity();
                staffCountEntity.setId(c.getLong(0));
                staffCountEntity.setPackCount(c.getInt(1));
                staffCountEntity.setProductCount(c.getInt(2));
                staffCountEntityList.add(staffCountEntity);
            }
        }

        if (staffCountEntityList!=null&&staffCountEntityList.size()>0){
            for (StaffCountEntity entity :staffCountEntityList) {
                Staff staff=staffDao.queryBuilder().where(StaffDao.Properties.Id.eq(entity)).unique();
                entity.setName(staff.getName());
            }
        }

        return staffCountEntityList;
    }



}
