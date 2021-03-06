package com.yunsu.service.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.exception.BaseException;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.service.DataServiceImpl;
import com.yunsu.service.FileUpLoadService;
import com.yunsu.sqlite.MyDataBaseHelper;
import com.yunsu.sqlite.SQLiteOperation;
import com.yunsu.util.Constants;
import com.yunsu.util.KeyGenerator;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InitDataService extends Service{
    public static final String TAG = "SyncFileService";

    public InitDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        insertData();
    }

    private void insertData() {
        MyDataBaseHelper dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
        String actionId=Constants.Logistic.INBOUND_CODE;
        String agencyId=Constants.DEFAULT_STORAGE;
         SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         Date date=new Date();
        for (int i=0;i<100;i++){
            String packKey= KeyGenerator.getNew();
            SQLiteOperation.insertPathData(dataBaseHelper.getWritableDatabase(),
                    packKey,actionId,agencyId,Constants.DB.NOT_SYNC,dateFormat.format(date));
        }

        dataBaseHelper.close();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
