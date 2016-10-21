package com.yunsu.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Frank Zhou on 2015/7/14.
 */
public class PackDataBaseHelper extends SQLiteOpenHelper{

    final String CREATE_PACK_TABLE_SQL =
            "create table pack(_id integer primary key autoincrement , pack_key unique,last_save_time)";
    final String CREATE_PACK_PRODUCT_TABLE_SQL=
            "create table pack(_id integer primary key autoincrement , product_key unique, pack_key,last_save_time)";

    public PackDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public PackDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PACK_TABLE_SQL);
        db.execSQL(CREATE_PACK_PRODUCT_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
