package com.yunsoo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsoo.util.Constants;


public class SQLiteManager extends BaseManager {

    private static SQLiteManager dataBaseManager;

    public static SQLiteManager initializeIntance(Context context) {

        if (dataBaseManager == null) {
            synchronized (SQLiteManager.class) {
                if (dataBaseManager == null) {
                    dataBaseManager = new SQLiteManager();
                    dataBaseManager.context = context;
                }
            }
        }
        return dataBaseManager;
    }

    public static SQLiteManager getInstance() {
        if (dataBaseManager == null) {
            Log.w("SQLiteDataBaseManager", "SQLiteDataBaseManager instance has not been initialized");
        }
        return dataBaseManager;
    }

    public  void savePackLastId(int id){
        SharedPreferences.Editor editor = dataBaseManager.context.getSharedPreferences(Constants.Preference.PREF_SQLITE, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.SQ_PACK_LAST_ID, id);
        editor.commit();
    }

    public  int getPackLastId(){
        SharedPreferences preferences = dataBaseManager.context.getSharedPreferences(Constants.Preference.PREF_SQLITE,
                Context.MODE_PRIVATE);
        return preferences.getInt(Constants.Preference.SQ_PACK_LAST_ID, 0);
    }

//    public  void savePathLastId(int id){
//        SharedPreferences.Editor editor = dataBaseManager.context.getSharedPreferences(Constants.Preference.PREF_SQLITE, Context.MODE_PRIVATE).edit();
//        editor.putInt(Constants.Preference.SQ_PATH_LAST_ID, id);
//        editor.commit();
//    }

    public  void savePathLastId(int actionId,int id){
        SharedPreferences.Editor editor = dataBaseManager.context.getSharedPreferences(Constants.Preference.PREF_SQLITE, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.SQ_PATH_LAST_ID+"_"+actionId, id);
        editor.commit();
    }

//    public  int getPathLastId(){
//        SharedPreferences preferences = dataBaseManager.context.getSharedPreferences(Constants.Preference.PREF_SQLITE,
//                Context.MODE_PRIVATE);
//        return preferences.getInt(Constants.Preference.SQ_PATH_LAST_ID, 0);
//    }

    public  int getPathLastId(int actionId){
        SharedPreferences preferences = dataBaseManager.context.getSharedPreferences(Constants.Preference.PREF_SQLITE,
                Context.MODE_PRIVATE);
        return preferences.getInt(Constants.Preference.SQ_PATH_LAST_ID+"_"+actionId, 0);
    }
}
