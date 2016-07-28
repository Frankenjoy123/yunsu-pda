package com.yunsoo.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.forlong401.log.transaction.log.manager.LogManager;
import com.yunsoo.manager.DeviceManager;
import com.yunsoo.manager.FileManager;
import com.yunsoo.manager.LogisticManager;
import com.yunsoo.manager.SQLiteManager;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.manager.SettingManager;
import com.yunsoo.network.CacheService;
import com.yunsoo.network.NetworkManager;
import com.yunsoo.service.background.LogService;

import okhttp3.OkHttpClient;

/**
 * Created by Frank zhou on 2015/7/20.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext=this;

        Intent stateService =  new Intent(this,LogService.class);
        startService(stateService );

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
