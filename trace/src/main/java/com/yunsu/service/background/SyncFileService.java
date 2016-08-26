package com.yunsu.service.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.exception.BaseException;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.FileUpLoadService;
import com.yunsu.common.util.Constants;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncFileService extends Service implements DataServiceImpl.DataServiceDelegate{
    public static final String TAG = "SyncFileService";

    public SyncFileService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        startSync();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startSync(){
        Timer timer = new Timer();
        final Context context=this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogisticManager.getInstance().createLogisticFile();
                List<String> fileNames=FileManager.getInstance().getPackFileNames();
                if (fileNames!=null&&fileNames.size()>0){
                    String folderName = android.os.Environment.getExternalStorageDirectory() +
                            Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
                    File path_task_folder = new File(folderName);
                    File[] files=path_task_folder.listFiles();
                    for(int i=0;i<files.length;i++){
                        FileUpLoadService fileUpLoadService=new FileUpLoadService(files[i].getAbsolutePath());
                        fileUpLoadService.setFileType(FileUpLoadService.PATH_FILE);
                        fileUpLoadService.setIndex(i);
                        fileUpLoadService.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                        fileUpLoadService.start();
                    }
                }

            }
        },0,1000*60* SettingManager.getInstance().getSyncRateMin());
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
        if (service instanceof FileUpLoadService){

            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_SUCCESS_FOLDER;
            File path_success_folder = new File(folderName);
            if (!path_success_folder.exists()){
                path_success_folder.mkdirs();
            }

            File oldFile=new File(((FileUpLoadService) service).getFilePath());
            File newFile=new File(path_success_folder,oldFile.getName());
            oldFile.renameTo(newFile);

        }
    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {

    }
}
