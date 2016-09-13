package com.yunsu.activity;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.network.CacheService;
import com.yunsu.common.network.NetworkManager;
import com.yunsu.common.util.Constants;
import com.yunsu.common.config.ConfigureLog4j;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.common.util.CrashHandler;

/**
 * Created by Frank zhou on 2015/7/20.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Context appContext=this;

        GreenDaoManager.initializeIntance(appContext);

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

        String logPath=Environment.getExternalStorageDirectory()+ Constants.YUNSOO_FOLDERNAME
                + Constants.PATH_LOG_NOT_SYNC_FOLDER;
        ConfigureLog4j.configure(logPath);

        CrashHandler catchHandler = CrashHandler.getInstance();
        String path = Environment.getExternalStorageDirectory()+ Constants.YUNSOO_FOLDERNAME
                + Constants.PATH_CRASH_NOT_SYNC_FOLDER;
        catchHandler.init(appContext,path);

    }

//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        LogManager.getManager(getApplicationContext()).unregisterCrashHandler();
//    }
}
