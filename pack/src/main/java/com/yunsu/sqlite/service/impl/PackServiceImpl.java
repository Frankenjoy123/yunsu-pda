package com.yunsu.sqlite.service.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunsu.common.util.Constants;
import com.yunsu.entity.PackProductsEntity;
import com.yunsu.entity.StaffCountEntity;
import com.yunsu.greendao.dao.PackDao;
import com.yunsu.greendao.dao.ProductDao;
import com.yunsu.greendao.dao.StaffDao;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
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
    private ProductDao productDao=GreenDaoManager.getInstance().getDaoSession().getProductDao();


    private SQLiteDatabase db=GreenDaoManager.getInstance().getDb();
    @Override
    public long addPack(Pack pack) {
        return packDao.insert(pack);
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

//        Cursor c = db.rawQuery("select staff_id, count(*) , sum(real_count)  from Pack where date(last_save_time)=? group by staff_id",
//                new String[]{date});

        StringBuilder builder=new StringBuilder();
        builder.append("select staff_id, s.NAME, sum(p.real_count) as 'product count', count(distinct p._id) as 'pack count' ");
        builder.append("from Pack p inner join staff s on p.STAFF_ID = s._id ");
        builder.append("where  date(p.last_save_time)=? group by p.staff_id");

        Cursor c=db.rawQuery(builder.toString(),new String[]{date});

        List<StaffCountEntity> staffCountEntityList=new ArrayList<>();

        if (c!=null){
            while(c.moveToNext()) {
                StaffCountEntity staffCountEntity=new StaffCountEntity();
                staffCountEntity.setId(c.getLong(0));
                staffCountEntity.setName(c.getString(1));
                staffCountEntity.setProductCount(c.getInt(2));
                staffCountEntity.setPackCount(c.getInt(3));
                staffCountEntityList.add(staffCountEntity);
            }
        }

        return staffCountEntityList;
    }

    @Override
    public List<String> queryNotCommitDateList() {
        StringBuilder builder=new StringBuilder();
        builder.append("select distinct date(p.last_save_time) ");
        builder.append(" from Pack p ");
        builder.append(" where  p.status != ?");

        Cursor c=db.rawQuery(builder.toString(),new String[]{Constants.DB.COMMIT});

        List<String> dateList=new ArrayList<>();

        if (c!=null){
            while(c.moveToNext()) {
                dateList.add(c.getString(0));
            }
        }

        return dateList;
    }

    @Override
    public List<PackProductsEntity> queryPackProductsByDate(String date) {
        List<PackProductsEntity> packProductsEntityList =new ArrayList<>();
        StringBuilder builder=new StringBuilder();
        builder.append("select  p._id as 'pack id' , p.PACK_KEY as 'pack key' , p.last_save_time , p.standard,  p.real_count , p.staff_id , p.product_base_id , p.status , group_concat(pr.product_key) as 'product keys'  ");
        builder.append(" from Pack p inner join product pr on pr.PACK_ID = p._id ");
        builder.append("where  date(p.last_save_time)=? and p.status != 'commit' group by p._id");
        Cursor c=db.rawQuery(builder.toString(),new String[]{date});

        if (c!=null){
            while(c.moveToNext()) {
                PackProductsEntity entity=new PackProductsEntity();
                Pack pack =new Pack();
                pack.setId(c.getLong(0));
                pack.setPackKey(c.getString(1));
                pack.setLastSaveTime(c.getString(2));
                pack.setStandard(c.getInt(3));
                pack.setRealCount(c.getInt(4));
                pack.setStaffId(c.getLong(5));
                pack.setProductBaseId(c.getLong(6));
                pack.setStatus(c.getString(7));
                entity.setPack(pack);
                entity.setProductsString(c.getString(8));

                packProductsEntityList.add(entity);
            }
        }


        return packProductsEntityList;
    }

    @Override
    public void updatePacksStatus(String date, String status) {

    }

    @Override
    public void updateInTx(List<Pack> packList) {
        packDao.updateInTx(packList);
    }

}
