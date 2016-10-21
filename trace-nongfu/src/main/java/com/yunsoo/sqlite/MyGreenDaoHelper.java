package com.yunsu.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yunsu.greendao.dao.DaoMaster;

/**
 * Created by yunsu on 2016/8/10.
 */
public class MyGreenDaoHelper extends DaoMaster.OpenHelper {
    public MyGreenDaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
