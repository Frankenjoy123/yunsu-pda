package com.yunsu.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.yunsu.common.manager.DeviceManager;
import com.yunsu.config.ConfigureLog4j;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.common.network.CacheService;
import com.yunsu.common.network.NetworkManager;
import com.yunsu.common.util.Constants;
import com.yunsu.service.background.SyncLogService;
import com.yunsu.util.CrashHandler;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by Frank zhou on 2015/7/20.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext=this;



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

        ConfigureLog4j.configure();

        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(appContext);

    }

//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        LogManager.getManager(getApplicationContext()).unregisterCrashHandler();
//    }
}
