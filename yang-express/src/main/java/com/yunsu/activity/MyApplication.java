package com.yunsu.activity;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.yunsu.common.config.ConfigureLog4j;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.manager.FileLocationManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.network.CacheService;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.CrashHandler;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.manager.SettingManager;

/**
 * Created by Frank zhou on 2015/7/20.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext=this;

        YunsuKeyUtil.initializeIntance(appContext);

        GreenDaoManager.initializeIntance(appContext);
        SettingManager settingManager=SettingManager.initializeInstance(appContext);
        settingManager.restore();

        SessionManager sessionManager = SessionManager.initializeIntance(appContext);
        sessionManager.restore();

        DeviceManager deviceManager= DeviceManager.initializeIntance(appContext);

        FileLocationManager.initializeIntance(appContext);

//        NetworkManager.initializeIntance(appContext).isNetworkConnected();

        CacheService.initializeInstance(appContext);

        FileManager.initializeIntance(appContext);

        SQLiteManager.initializeIntance(appContext);

        String logPath=FileLocationManager.getInstance().getCommonLogTaskFolder();
        ConfigureLog4j.configure(logPath);

        CrashHandler catchHandler = CrashHandler.getInstance();
        String path = FileLocationManager.getInstance().getCrashLogTaskFolder();
        catchHandler.init(appContext,path);

    }
}
