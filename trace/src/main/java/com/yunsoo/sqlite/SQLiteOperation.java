package com.yunsoo.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Frank zhou on 2015/7/14.
 */
public class SQLiteOperation {
    public static void insertPackData(SQLiteDatabase db,String pack_key,String product_keys,String time){
        db.execSQL("insert into pack values(null,?,?,?)",new Object[]{pack_key,product_keys,time});
    }
    public static void insertPathData(SQLiteDatabase db,String pack_key,String action_id,String time){
        db.execSQL("insert into path values(null,?,?,?)",new String[]{pack_key,action_id,time});
    }
}
