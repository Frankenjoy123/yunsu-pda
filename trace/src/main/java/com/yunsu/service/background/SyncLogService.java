package com.yunsu.service.background;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.exception.BaseException;
import com.yunsu.manager.FileManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.service.LogUpLoadService;
import com.yunsu.common.util.Constants;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncLogService extends IntentService implements DataServiceImpl.DataServiceDelegate{
    public static final String TAG = "SyncLogService";


    public SyncLogService( ) {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        startSync();
    }


    private void startSync(){

        //上传今天前的所有的普通日志文件
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder builder=new StringBuilder();
        builder.append(dateFormat.format(new Date()));
        builder.append(".log");
        final Context context=this;
        List<String> fileNames=FileManager.getInstance().getUnSyncLogFileNames();
        if (fileNames!=null&&fileNames.size()>0){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_NOT_SYNC_FOLDER;
            File path_task_folder = new File(folderName);
            File[] files=path_task_folder.listFiles();
            for(int i=0;i<files.length;i++){
                if ((files[i].getName().compareTo(builder.toString())<0)){
                    LogUpLoadService service=new LogUpLoadService(files[i].getAbsolutePath());
                    service.setIndex(i);
                    service.setCrashLog(false);
                    service.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                    service.start();
                }
            }
        }

        //上传全部crash文件
        try {
            String crashNotSyncFolderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_CRASH_NOT_SYNC_FOLDER;
            File crashNotSyncFolder = new File(crashNotSyncFolderName);
            if (crashNotSyncFolder.exists()){
                File[] crashNotSyncfiles= crashNotSyncFolder.listFiles();
                if (crashNotSyncfiles!=null&&crashNotSyncfiles.length>0){
                    for(int i=0;i<crashNotSyncfiles.length;i++){

                            LogUpLoadService service=new LogUpLoadService(crashNotSyncfiles[i].getAbsolutePath());
                            service.setIndex(i);
                            service.setCrashLog(true);
                            service.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                            service.start();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof LogUpLoadService){
            String folderName=null;
            if (((LogUpLoadService)service).isCrashLog()){
                 folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME+Constants.PATH_CRASH_SYNC_FOLDER;
            }else {
                 folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_SYNC_FOLDER;
            }
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
