package com.yunsu.service.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.yunsu.common.exception.BaseException;
import com.yunsu.common.network.NetworkManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.LogUpLoadService;
import com.yunsu.common.util.Constants;
import com.yunsu.entity.PackProductsEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.service.FileUpLoadService;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncFileService extends Service implements DataServiceImpl.DataServiceDelegate{
    public static final String TAG = "SyncFileService";

    ConnectivityManager manager;

    SimpleDateFormat dateFormat;

    PackService packService;

    public SyncFileService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        packService=new PackServiceImpl();
        dateFormat=new SimpleDateFormat(Constants.dateOnlyDayFormat);
        startSync();
    }


    private void startSync(){
        Timer timer = new Timer();
        final Context context=this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NetworkManager.getInstance().updateConnectStatus();
                if (NetworkManager.getInstance().isNetworkConnected()){
                    List<String> dateList=packService.queryNotCommitDateList();
                    for (String date : dateList){
                        //查询出状态不是commit的当天的数据
                        List<PackProductsEntity> packProductsEntityList=packService.queryPackProductsByDate(date);

                        String filePath=FileManager.getInstance().createPackFile(packProductsEntityList,date);

                        List<BasicNameValuePair> queryList=new ArrayList<BasicNameValuePair>();

                        String[] arrName=filePath.split("/");
                        if (arrName.length>0){
                            String fileName=arrName[arrName.length-1];
                            queryList.add(new BasicNameValuePair(FileUpLoadService.FILE_NAME,fileName));
                        }

                        if (date.compareTo(dateFormat.format(new Date()))<0){
                            queryList.add(new BasicNameValuePair(FileUpLoadService.COMMITTED,FileUpLoadService.TRUE));
                        }else {
                            queryList.add(new BasicNameValuePair(FileUpLoadService.COMMITTED,FileUpLoadService.FALSE));
                        }
                        queryList.add(new BasicNameValuePair(FileUpLoadService.IGNORED,FileUpLoadService.TRUE));

                        FileUpLoadService fileUpLoadService=new FileUpLoadService(filePath);
                        fileUpLoadService.setDelegate((DataServiceImpl.DataServiceDelegate) context);
                        fileUpLoadService.setQueryPairList(queryList);
                        fileUpLoadService.setPackProductsEntityList(packProductsEntityList);
                        fileUpLoadService.start();
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

    private boolean isFileCommitted( List<BasicNameValuePair> queryPairs){
        boolean committed=false;
        for (BasicNameValuePair pair :queryPairs){
            if (pair.getName().equals(FileUpLoadService.COMMITTED) && pair.getValue().equals(FileUpLoadService.TRUE)){
                committed=true;
            }
        }
        return committed;
    }

    private void updatePackListStatus(List<PackProductsEntity> entityList , String status){
        List<Pack> packList=new ArrayList<>();
        for (PackProductsEntity entity : entityList){
            Pack pack=entity.getPack();
            pack.setStatus(status);
            packList.add(pack);
        }
        if (packList.size()>0){
            packService.updateInTx(packList);
        }
    }


    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof FileUpLoadService){
            FileUpLoadService fileUpLoadService= (FileUpLoadService) service;
            List<BasicNameValuePair> queryPairs=fileUpLoadService.getQueryPairList();
            //判断是否需要更新状态为commit
            if (isFileCommitted(queryPairs)){
                //更新状态为commit
                updatePackListStatus(fileUpLoadService.getPackProductsEntityList(),Constants.DB.COMMIT);
            }else {
                //更新状态为sync
                updatePackListStatus(fileUpLoadService.getPackProductsEntityList(),Constants.DB.SYNC);
            }

            File oldFile=new File(((FileUpLoadService) service).getFilePath());
            if (oldFile.exists()){
                oldFile.delete();
            }
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
