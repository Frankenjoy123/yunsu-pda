package com.yunsu.sqlite.service.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunsu.greendao.dao.ExpressDao;
import com.yunsu.greendao.entity.Express;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.ExpressService;

import de.greenrobot.dao.query.Query;

/**
 * Created by yunsu on 2016/10/19.
 */
public class ExpressServiceImpl implements ExpressService {

    private ExpressDao expressDao= GreenDaoManager.getInstance().getDaoSession().getExpressDao();

    private SQLiteDatabase db=GreenDaoManager.getInstance().getDb();

    @Override
    public long addExpress(Express express) {
        return expressDao.insert(express);
    }

    @Override
    public Express QueryExpressByPackKey(String packKey) {
        Query query= expressDao.queryBuilder().where(ExpressDao.Properties.PackKey.eq(packKey)).build();
        return (Express) query.unique();
    }

    @Override
    public Express QueryExpressByExpressKey(String expressKey) {
        Query query= expressDao.queryBuilder().where(ExpressDao.Properties.ExpressKey.eq(expressKey)).build();
        return (Express) query.unique();
    }

    @Override
    public long queryExpressCount(String date) {

        long count=0;
        StringBuilder builder=new StringBuilder();
        builder.append("select count(*) ");
        builder.append(" from Express ");
        builder.append(" where  date(create_time)=?");
        Cursor c=db.rawQuery(builder.toString(),new String[]{date});
        if (c!=null){
            c.moveToFirst();
            count=c.getLong(0);
        }
        return count;
    }
}
