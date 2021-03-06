package com.yunsu.service.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.exception.BaseException;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.FileUpLoadService;
import com.yunsu.common.service.LogUpLoadService;
import com.yunsu.common.util.Constants;
import com.yunsu.entity.PackProductsEntity;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncFileService extends Service implements DataServiceImpl.DataServiceDelegate{
    public static final String TAG = "SyncFileService";

    ConnectivityManager manager;

    PackService packService;

    public SyncFileService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        packService=new PackServiceImpl();
        startSync();
    }

    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }

        return flag;
    }

    private void startSync(){
        Timer timer = new Timer();
        final Context context=this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (checkNetworkState()){
                    List<String> dateList=packService.queryNotCommitDateList();
                    for (String date : dateList){
                        List<PackProductsEntity> packProductsEntityList=packService.queryPackProductsByDate(date);
                        String filePath=FileManager.getInstance().createPackFile(packProductsEntityList);
                        FileUpLoadService fileUpLoadService=new FileUpLoadService(filePath);
                        fileUpLoadService.setFileType(FileUpLoadService.PACK_FILE);
                        fileUpLoadService.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                        fileUpLoadService.start();
                    }

                }



                List<String> fileNames=FileManager.getInstance().getPackFileNames();
                if (fileNames!=null&&fileNames.size()>0){
                    String folderName = android.os.Environment.getExternalStorageDirectory() +
                            Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_TASK_FOLDER;
                    File path_task_folder = new File(folderName);
                    File[] files=path_task_folder.listFiles();
                    for(int i=0;i<files.length;i++){
                        FileUpLoadService fileUpLoadService=new FileUpLoadService(files[i].getAbsolutePath());
                        fileUpLoadService.setFileType(FileUpLoadService.PACK_FILE);
                        fileUpLoadService.setIndex(i);
                        fileUpLoadService.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                        fileUpLoadService.start();
                    }
                }

                List<String> logFileNames=FileManager.getInstance().getUnSyncLogFileNames();
                if (logFileNames!=null&&logFileNames.size()>0){
                    String folderName = android.os.Environment.getExternalStorageDirectory() +
                            Constants.YUNSOO_FOLDERNAME+Constants.PACK_LOG_NOT_SYNC_FOLDER;
                    File path_task_folder = new File(folderName);
                    File[] files=path_task_folder.listFiles();
                    for(int i=0;i<files.length;i++){
                        LogUpLoadService service=new LogUpLoadService(files[i].getAbsolutePath());
                        service.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                        service.start();
                    }
                }

            }

            private void uploadAutoInboundPackFile() {
                String folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME+Constants.PACK_AUTO_INBOUND_TASK_FOLDER;

                try {
                    File path_task_folder = new File(folderName);
                    if (path_task_folder.exists()){
                        File[] files=path_task_folder.listFiles();
                        for(int i=0;i<files.length;i++){
                            FileUpLoadService fileUpLoadService=new FileUpLoadService(files[i].getAbsolutePath());
                            fileUpLoadService.setFileType(FileUpLoadService.PATH_FILE);
                            fileUpLoadService.setIndex(i);
                            fileUpLoadService.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                            fileUpLoadService.start();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        },0,1000*60* SettingManager.getInstance().getSyncRateMin());
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
        if (service instanceof FileUpLoadService){

            String folderName=null;
            if (((FileUpLoadService) service).getFileType().equals(FileUpLoadService.PACK_FILE)){

                folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_SUCCESS_FOLDER;
            }else if (((FileUpLoadService) service).getFileType().equals(FileUpLoadService.PATH_FILE)){

                folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME+Constants.PACK_AUTO_INBOUND_SUCCESS_FOLDER;
            }

            File path_success_folder = new File(folderName);
            if (!path_success_folder.exists()){
                path_success_folder.mkdirs();
            }

            File oldFile=new File(((FileUpLoadService) service).getFilePath());
            File newFile=new File(path_success_folder,oldFile.getName());
            oldFile.renameTo(newFile);
        }

        if (service instanceof LogUpLoadService){

            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_LOG_SYNC_FOLDER;
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
