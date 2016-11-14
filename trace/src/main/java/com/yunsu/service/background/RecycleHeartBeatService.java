package com.yunsu.service.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.HeartBeatService;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.common.util.Constants;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class RecycleHeartBeatService extends Service implements DataServiceImpl.DataServiceDelegate {
    public static final String TAG = "RecycleHeartBeatService";

    public RecycleHeartBeatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        startSync();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startSync(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                checkAuthorizeStatus();

            }
        },0,1000*60*2);
    }

    /**
     * 检查授权状态
     */
    private void checkAuthorizeStatus() {
        boolean isAuthorize=getSharedPreferences(Constants.Preference.YUNSU_PDA,MODE_PRIVATE)
                .getBoolean(Constants.Preference.IS_AUTHORIZE,false);
        if (isAuthorize){
            AuthUser authUser=SessionManager.getInstance().getAuthUser();
            PermanentTokenLoginService service= new PermanentTokenLoginService(authUser.getPermanentToken());
            service.setDelegate(this);
            service.setContext(this);
            service.start();
        }
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

    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof PermanentTokenLoginService){
            HeartBeatService heartBeatService=new HeartBeatService();
            heartBeatService.start();
        }
    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {

    }
}
