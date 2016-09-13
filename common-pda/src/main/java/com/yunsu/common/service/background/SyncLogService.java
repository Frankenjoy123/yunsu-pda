package com.yunsu.common.service.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.yunsu.common.exception.BaseException;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.LogUpLoadService;
import com.yunsu.common.util.Constants;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncLogService extends IntentService implements DataServiceImpl.DataServiceDelegate{
    public static final String TAG = "SyncLogService";


    public SyncLogService( ) {
        super(TAG);
    }

    private String commonLogNotSyncPath;

    private String commonLogSyncPath;

    private String crashLogNotSyncPath;

    private String crashLogSyncPath;


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra(Constants.APP_TYPE).equals(Constants.TRACE_APP)){
            commonLogNotSyncPath= Constants.PATH_LOG_NOT_SYNC_FOLDER;
            commonLogSyncPath= Constants.PATH_LOG_SYNC_FOLDER;;
            crashLogNotSyncPath=Constants.PATH_CRASH_NOT_SYNC_FOLDER;
            crashLogSyncPath=Constants.PATH_CRASH_SYNC_FOLDER;
        }else {
            commonLogNotSyncPath= Constants.PACK_LOG_NOT_SYNC_FOLDER;
            commonLogSyncPath= Constants.PACK_LOG_SYNC_FOLDER;;
            crashLogNotSyncPath=Constants.PACK_CRASH_NOT_SYNC_FOLDER;
            crashLogSyncPath=Constants.PACK_CRASH_SYNC_FOLDER;
        }
        startSync();
    }


    private void startSync(){

        //上传今天前的所有的普通日志文件
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder builder=new StringBuilder();
        builder.append(dateFormat.format(new Date()));
        builder.append(".log");
        final Context context=this;
        File path_task_folder = new File(Environment.getExternalStorageDirectory()
                + Constants.YUNSOO_FOLDERNAME+commonLogNotSyncPath);
        if (path_task_folder.exists()){
            File[] files=path_task_folder.listFiles();
            if (files!=null && files.length>0){
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

        }


        //上传全部crash文件
        try {
            File crashNotSyncFolder = new File(Environment.getExternalStorageDirectory()
                    + Constants.YUNSOO_FOLDERNAME+crashLogNotSyncPath);
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
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String folderName=null;
            if (((LogUpLoadService)service).isCrashLog()){
                 folderName = Environment.getExternalStorageDirectory()+ Constants.YUNSOO_FOLDERNAME+crashLogSyncPath;
            }else {
                 folderName = Environment.getExternalStorageDirectory()+ Constants.YUNSOO_FOLDERNAME+commonLogSyncPath;
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
