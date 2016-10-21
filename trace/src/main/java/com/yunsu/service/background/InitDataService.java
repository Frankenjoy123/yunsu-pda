package com.yunsu.service.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.sqlite.MyDataBaseHelper;
import com.yunsu.common.util.Constants;


import java.text.SimpleDateFormat;
import java.util.Date;

public class InitDataService extends Service{
    public static final String TAG = "SyncFileService";

    public InitDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                insertData();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }



    private void insertData() {
        MyDataBaseHelper dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
        String actionId=Constants.Logistic.INBOUND_CODE;
        String agencyId=Constants.DEFAULT_STORAGE;
         SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         Date date=new Date();
        for (int i=0;i<100;i++){
//            String packKey= KeyGenerator.getNew();
//            SQLiteOperation.insertPathData(dataBaseHelper.getWritableDatabase(),
//                    packKey,actionId,agencyId,Constants.DB.NOT_SYNC,dateFormat.format(date));
        }

        dataBaseHelper.close();
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
