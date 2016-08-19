package com.yunsu.activity;

import android.app.Application;
import android.content.Context;

import com.yunsu.manager.DeviceManager;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.manager.SessionManager;
import com.yunsu.network.CacheService;
import com.yunsu.network.NetworkManager;

/**
 * Created by Frank zhou on 2015/7/20.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext=this;
        SessionManager sessionManager = SessionManager.initializeIntance(appContext);
        sessionManager.restore();
        DeviceManager deviceManager= DeviceManager.initializeIntance(appContext);
        NetworkManager.initializeIntance(appContext).isNetworkConnected();

        CacheService.initializeInstance(appContext);

        FileManager.initializeIntance(appContext);

        SQLiteManager.initializeIntance(appContext);

        LogisticManager logisticManager=LogisticManager.initializeInstance(appContext);
        logisticManager.restore();

    }
}
