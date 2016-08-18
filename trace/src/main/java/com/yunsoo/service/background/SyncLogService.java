package com.yunsoo.service.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.exception.BaseException;
import com.yunsoo.manager.FileManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsoo.service.LogUpLoadService;
import com.yunsu.common.util.Constants;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class SyncLogService extends Service implements DataServiceImpl.DataServiceDelegate{
    public static final String TAG = "SyncLogService";

    public SyncLogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        startSync();
    }

    private void startSync(){
        final Context context=this;
        List<String> fileNames=FileManager.getInstance().getUnSyncLogFileNames();
        if (fileNames!=null&&fileNames.size()>0){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_NOT_SYNC_FOLDER;
            File path_task_folder = new File(folderName);
            File[] files=path_task_folder.listFiles();
            for(int i=0;i<files.length;i++){
                LogUpLoadService service=new LogUpLoadService(files[i].getAbsolutePath());
                service.setIndex(i);
                service.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                service.start();
            }
        }
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

    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof LogUpLoadService){

            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_SYNC_FOLDER;
            File path_success_folder = new File(folderName);
            if (!path_success_folder.exists()){
                path_success_folder.mkdirs();
            }

            File oldFile=new File(((LogUpLoadService) service).getFilePath());
            File newFile=new File(path_success_folder,oldFile.getName());
            oldFile.renameTo(newFile);

        }
    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {

    }
}
