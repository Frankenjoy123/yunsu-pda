package com.yunsoo.service.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsoo.service.HeartBeatService;

import java.util.Timer;
import java.util.TimerTask;

public class RecycleHeartBeatService extends Service{
    public static final String TAG = "RecycleHeartBeatService";

    public RecycleHeartBeatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startSync();
    }

    private void startSync(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HeartBeatService service=new HeartBeatService();
                service.start();
            }
        },0,1000*60*2);
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
