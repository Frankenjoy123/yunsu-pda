package com.yunsu.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsu.common.manager.BaseManager;
import com.yunsu.common.util.Constants;

/**
 * Created by yunsu on 2016/7/14.
 */
public class SettingManager extends BaseManager {
    private static SettingManager manager;

    private static int syncRateMin;

    private static boolean isAutoInbound;

    public static SettingManager initializeInstance(Context context) {

        if (manager == null) {
            synchronized (SettingManager.class) {
                if (manager == null) {
                    manager = new SettingManager();
                    manager.context = context;
                }
            }
        }
        return manager;
    }

    public static SettingManager getInstance() {
        if (manager == null) {
            Log.w("SessionManager", "SettingManager instance has not been initialized");
        }
        return manager;
    }

    public void restore() {
        SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.SETTING,
                Context.MODE_PRIVATE);
        syncRateMin = preferences.getInt(Constants.Preference.SYNC_RATE, 30);
        isAutoInbound=preferences.getBoolean(Constants.Preference.AUTO_INBOUND,false);
    }

    public void saveAutoInboundSetting(boolean isAutoInbound) {
        this.isAutoInbound=isAutoInbound;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.Preference.SETTING, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.Preference.AUTO_INBOUND, isAutoInbound);
        editor.commit();
    }


    public void saveSyncRateSetting(int min) {
        this.setSyncRateMin(min);
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.Preference.SETTING, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.SYNC_RATE, min);
        editor.commit();
    }

    public  boolean isAutoInbound() {
        return isAutoInbound;
    }

    public  int getSyncRateMin() {
        return syncRateMin;
    }

    private  void setSyncRateMin(int syncRateMin) {
        this.syncRateMin = syncRateMin;
    }
}
