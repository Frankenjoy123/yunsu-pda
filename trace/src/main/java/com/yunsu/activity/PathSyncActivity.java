package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.FileSyncAdapter;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.manager.FileManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.FileUpLoadService;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.sqlite.MyDataBaseHelper;
import com.yunsu.common.util.Constants;
import com.yunsu.common.view.TitleBar;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 原同步界面，暂停用
 */
public class PathSyncActivity extends BaseActivity implements DataServiceImpl.DataServiceDelegate {

    private MyDataBaseHelper dataBaseHelper;
    private ListView lv_path_sync;
    private TitleBar titleBar;
    private TextView tv_empty_file_tip;
    private FileSyncAdapter adapter;

    //    private int minIndex;
    private int maxIndex;

    private List<String> fileNames;
    private List<Integer> status;

    private List<Map<String,String>> actionList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_sync);
        getActionBar().hide();
        final Context context=this;
        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
//                LogisticManager.getInstance().createLogisticFile(context);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getPackFileNames();
                    }
                });
            }
        }).start();

    }


    private void init() {
        lv_path_sync = (ListView) findViewById(R.id.lv_path_sync);
        tv_empty_file_tip= (TextView) findViewById(R.id.tv_path_empty_tip);
        titleBar= (TitleBar) findViewById(R.id.path_sync_title_bar);
        titleBar.setTitle(getString(R.string.path_sync));
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setDisplayAsBack(true);
        titleBar.setRightButtonText(getString(R.string.sync));
        adapter=new FileSyncAdapter(this);
        fileNames=new ArrayList<>();
        status=new ArrayList<>();
        adapter.setFileNames(fileNames);
        adapter.setStatus(status);

        lv_path_sync.setAdapter(adapter);


        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileNames!=null&&fileNames.size()>0  && !FileManager.getInstance().isAllFileUpload(status)){
                    String[] titleArray = new String[]{getString(R.string.off_line_upload),
                            getString(R.string.wifi_upload)};
                    AlertDialog dialog = new AlertDialog.Builder(PathSyncActivity.this)
                            .setItems(titleArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i==0){
                                        Intent intent=new Intent(PathSyncActivity.this,OffLineUploadActivity.class);
                                        intent.putExtra(OffLineUploadActivity.OFFLINE_TYPE,OffLineUploadActivity.PATH_TYPE);
                                        startActivity(intent);
                                    }
                                    else if(i==1){
                                        uploadFiles();
                                    }
                                }
                            })
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.cancel, null).create();
                    dialog.show();
                }
            }
        });
    }

    private void uploadFiles() {
        showLoading();
        String folderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
        File pack_task_folder = new File(folderName);
        File[] files=pack_task_folder.listFiles();
        for(int i=0;i<files.length;i++){
            status.set(i,1);
            FileUpLoadService service=new FileUpLoadService(files[i].getAbsolutePath());
            service.setFileType(FileUpLoadService.PATH_FILE);
            service.setIndex(i);
            service.setDelegate(this);
            service.start();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestSucceeded(final DataServiceImpl service, JSONObject data, boolean isCached) {
        super.onRequestSucceeded(service, data, isCached);
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    int index=((FileUpLoadService) service).getIndex();
                    status.set(index,2);
                    adapter.notifyDataSetChanged();
                    if ( !FileManager.getInstance().isAllFileUpload(status)){
                        hideLoading();
                    }
                }
            });
        }

        if (service instanceof PermanentTokenLoginService){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
            File path_task_folder = new File(folderName);
            File[] files=path_task_folder.listFiles();
            for(int i=0;i<files.length;i++){
                status.set(i,1);
                FileUpLoadService fileUpLoadService=new FileUpLoadService(files[i].getAbsolutePath());
                fileUpLoadService.setFileType(FileUpLoadService.PATH_FILE);
                fileUpLoadService.setIndex(i);
                fileUpLoadService.setDelegate(this);
                fileUpLoadService.start();
            }
        }
    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {
        super.onRequestFailed(service, exception);
        if (exception instanceof ServerAuthException && service instanceof FileUpLoadService){
            PermanentTokenLoginService service1=new PermanentTokenLoginService(SessionManager.getInstance().
                    getAuthUser().getPermanentToken());
            service1.setDelegate(this);
            service1.start();
        }
    }

    private void getPackFileNames() {
        try {
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
            File pack_task_folder = new File(folderName);
            String[] packFiles= pack_task_folder.list();
            if (packFiles!=null&&packFiles.length>0){
                for (int i=0;i<packFiles.length;i++){
                    fileNames.add(packFiles[i]);
                    status.add(0);
                }
                lv_path_sync.setEmptyView(tv_empty_file_tip);
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
