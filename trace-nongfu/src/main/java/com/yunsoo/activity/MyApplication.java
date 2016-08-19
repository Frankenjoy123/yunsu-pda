package com.yunsu.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.forlong401.log.transaction.log.manager.LogManager;
import com.yunsu.manager.DeviceManager;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.manager.SessionManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.network.CacheService;
import com.yunsu.network.NetworkManager;
import com.yunsu.service.background.LogService;
import com.yunsu.service.background.SyncFileService;
import com.yunsu.service.background.SyncLogService;

/**
 * Created by Frank zhou on 2015/7/20.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext=this;

//        Intent stateService =  new Intent(this,LogService.class);
//        startService(stateService );

        GreenDaoManager.initializeIntance(appContext);

//        LogManager.getManager(getApplicationContext()).registerCrashHandler();
//        SQLiteManager.initializeIntance(appContext);

        SessionManager sessionManager = SessionManager.initializeIntance(appContext);
        sessionManager.restore();

        SettingManager settingManager=SettingManager.initializeInstance(appContext);
        settingManager.restore();

        DeviceManager deviceManager= DeviceManager.initializeIntance(appContext);
        NetworkManager.initializeIntance(appContext).isNetworkConnected();

        CacheService.initializeInstance(appContext);

        FileManager.initializeIntance(appContext);

        SQLiteManager.initializeIntance(appContext);

        LogisticManager logisticManager=LogisticManager.initializeInstance(appContext);
        logisticManager.restore();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogManager.getManager(getApplicationContext()).unregisterCrashHandler();
    }
}
