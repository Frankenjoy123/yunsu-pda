package com.yunsu.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yunsu.sqlite.MyGreenDaoHelper;
import com.yunsu.common.util.Constants;
import com.yunsu.common.manager.BaseManager;
import com.yunsu.greendao.dao.DaoMaster;
import com.yunsu.greendao.dao.DaoSession;


public class GreenDaoManager extends BaseManager {

    private static GreenDaoManager dataBaseManager;
    private static MyGreenDaoHelper helper;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;


    public static GreenDaoManager initializeIntance(Context context) {

        if (dataBaseManager == null) {
            synchronized (GreenDaoManager.class) {
                if (dataBaseManager == null) {
                    dataBaseManager = new GreenDaoManager();
                    dataBaseManager.context = context;
                    // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
                    // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
                    // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
                    // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
                    helper = new MyGreenDaoHelper(context, Constants.DB.DB_NAME, null);
                    db = helper.getWritableDatabase();
                    // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
                    daoMaster = new DaoMaster(db);
                    daoSession = daoMaster.newSession();
                }
            }
        }
        return dataBaseManager;
    }

    public static GreenDaoManager getInstance() {
        if (dataBaseManager == null) {
            Log.w("SQLiteDataBaseManager", "SQLiteDataBaseManager instance has not been initialized");
        }
        return dataBaseManager;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
