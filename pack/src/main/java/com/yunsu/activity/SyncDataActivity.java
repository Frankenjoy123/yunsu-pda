package com.yunsu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackProductsEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.manager.FileManager;
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

public class SyncDataActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.btn_sync_data)
    private Button btn_sync_data;

    @ViewById(id = R.id.tv_count_value)
    private TextView tv_count_value;

    @ViewById(id = R.id.tv_history_count_value)
    private TextView tv_history_count_value;

    private PackService packService;

    private long todayNotSyncCount =0;

    private long historyNotSyncCount=0;

    private SimpleDateFormat dateFormat;

    private List<PackProductsEntity> packProductsEntityList;

    private final static int QUERY_PACKS_MSG=201;

    private final static int SYNC_DATA_MSG=203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.sync_data));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        packService=new PackServiceImpl();

        dateFormat=new SimpleDateFormat(Constants.dateOnlyDayFormat);

        btn_sync_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLoading();

                ServiceExecutor.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (packProductsEntityList!=null && packProductsEntityList.size()>0){

                            String filePath= FileManager.getInstance().createPackFile(packProductsEntityList,dateFormat.format(new Date()));

                            List<BasicNameValuePair> queryList=new ArrayList<BasicNameValuePair>();

                            String[] arrName=filePath.split("/");
                            if (arrName.length>0){
                                String fileName=arrName[arrName.length-1];
                                queryList.add(new BasicNameValuePair(FileUpLoadService.FILE_NAME,fileName));
                            }

                            queryList.add(new BasicNameValuePair(FileUpLoadService.COMMITTED,FileUpLoadService.TRUE));

                            queryList.add(new BasicNameValuePair(FileUpLoadService.IGNORED,FileUpLoadService.TRUE));

                            FileUpLoadService fileUpLoadService=new FileUpLoadService(filePath);
                            fileUpLoadService.setDelegate( SyncDataActivity.this);
                            fileUpLoadService.setQueryPairList(queryList);
                            fileUpLoadService.start();

                        }
                    }
                });

            }
        });

        showLoading();

        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                packProductsEntityList= packService.queryPackProductsByDate(dateFormat.format(new Date()));
                todayNotSyncCount =packProductsEntityList.size();
                historyNotSyncCount=packService.queryUnCommitPackCountBeforeDate(dateFormat.format(new Date()));
                handler.sendEmptyMessage(QUERY_PACKS_MSG);
            }
        });

    }


    private void refreshUI(){

        tv_count_value.setText(String.valueOf(todayNotSyncCount));
        tv_history_count_value.setText(String.valueOf(historyNotSyncCount));
        if (todayNotSyncCount >0){
            btn_sync_data.setEnabled(true);
        }else {
            btn_sync_data.setEnabled(false);
        }

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_PACKS_MSG:
                    hideLoading();
                    refreshUI();
                    break;

                case SYNC_DATA_MSG:
                    hideLoading();
                    refreshUI();
                    break;

            }

            super.handleMessage(msg);
        }
    };


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
            //更新状态为commit
            updatePackListStatus(packProductsEntityList,Constants.DB.COMMIT);

            File oldFile=new File(((FileUpLoadService) service).getFilePath());
            if (oldFile.exists()){
                oldFile.delete();
            }

            todayNotSyncCount =0;

            handler.sendEmptyMessage(SYNC_DATA_MSG);

        }

    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {
        if (service instanceof FileUpLoadService){
            File oldFile=new File(((FileUpLoadService) service).getFilePath());
            if (oldFile.exists()){
                oldFile.delete();
            }
        }
        super.onRequestFailed(service,exception);
    }

}
