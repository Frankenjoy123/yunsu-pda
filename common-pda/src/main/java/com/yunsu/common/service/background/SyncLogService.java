package com.yunsu.common.service.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.yunsu.common.exception.BaseException;
import com.yunsu.common.manager.FileLocationManager;
import com.yunsu.common.network.NetworkManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.LogUpLoadService;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncLogService extends IntentService implements DataServiceImpl.DataServiceDelegate {
    public static final String TAG = "SyncLogService";


    public SyncLogService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        NetworkManager.getInstance().updateConnectStatus();

        if (NetworkManager.getInstance().isNetworkConnected()){
            startSync();
        }

    }


    private void startSync() {

        //上传今天前的所有的普通日志文件
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date()));
        builder.append(".log");
        final Context context = this;
        File path_task_folder = new File(FileLocationManager.getInstance().getCommonLogTaskFolder());
        if (path_task_folder.exists()) {
            File[] files = path_task_folder.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    if ((files[i].getName().compareTo(builder.toString()) < 0)) {
                        LogUpLoadService service = new LogUpLoadService(files[i].getAbsolutePath());
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
            File crashNotSyncFolder = new File(FileLocationManager.getInstance().getCrashLogTaskFolder());
            if (crashNotSyncFolder.exists()) {
                File[] crashNotSyncfiles = crashNotSyncFolder.listFiles();
                if (crashNotSyncfiles != null && crashNotSyncfiles.length > 0) {
                    for (int i = 0; i < crashNotSyncfiles.length; i++) {
                        LogUpLoadService service = new LogUpLoadService(crashNotSyncfiles[i].getAbsolutePath());
                        service.setIndex(i);
                        service.setCrashLog(true);
                        service.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                        service.start();
                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(SyncLogService.class).error(e.getMessage());
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof LogUpLoadService) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String folderName = null;
            if (((LogUpLoadService) service).isCrashLog()) {
                folderName = FileLocationManager.getInstance().getCrashLogSuccessFolder();
            } else {
                folderName = FileLocationManager.getInstance().getCommonLogSuccessFolder();
            }
            File path_success_folder = new File(folderName);
            if (!path_success_folder.exists()) {
                path_success_folder.mkdirs();
            }

            File oldFile = new File(((LogUpLoadService) service).getFilePath());
            File newFile = new File(path_success_folder, oldFile.getName());
            oldFile.renameTo(newFile);

        }
    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {

    }
}
