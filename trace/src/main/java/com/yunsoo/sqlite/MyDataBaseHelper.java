package com.yunsoo.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Frank Zhou on 2015/7/14.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper{

    final String CREATE_PACK_TABLE_SQL =
            "create table pack(_id integer primary key autoincrement , pack_key, product_keys,last_save_time)";
    final String CREATE_PATH_TABLE_SQL =
            "create table path(_id integer primary key autoincrement , pack_key, action_id,agency,status,last_save_time)";

    private final String CREATE_INDEX_SQL="create  index MY_INDEX on path (last_save_time, action_id,agency)";
    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_PACK_TABLE_SQL);
        db.execSQL(CREATE_PATH_TABLE_SQL);
        db.execSQL(CREATE_INDEX_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(CREATE_PACK_TABLE_SQL);
//        db.execSQL(CREATE_PATH_TABLE_SQL);
        db.execSQL(CREATE_INDEX_SQL);
    }
}
