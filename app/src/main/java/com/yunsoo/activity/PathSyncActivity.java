package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.FileSyncAdapter;
import com.yunsu.exception.BaseException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.manager.DeviceManager;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.manager.SessionManager;
import com.yunsu.service.DataServiceImpl;
import com.yunsu.service.FileUpLoadService;
import com.yunsu.service.PermanentTokenLoginService;
import com.yunsu.sqlite.MyDataBaseHelper;
import com.yunsu.util.Constants;
import com.yunsu.util.YSFile;
import com.yunsu.view.TitleBar;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private List<Map<Integer,String>> actionList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_sync);

        getActionBar().hide();

        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                createFile();
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

    private void createFile() {
        actionList=LogisticManager.getInstance().getActionList();
        dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
        Cursor cursor= null;

        for (int i=0;i<actionList.size();i++){
            int actionId=actionList.get(i).keySet().iterator().next();
            do {
                try {
                    cursor = dataBaseHelper.getReadableDatabase()
                            .rawQuery("select * from path where _id>? and action_id=? limit 1000",
                                    new String[]{String.valueOf(SQLiteManager.getInstance().getPathLastId(actionId)), String.valueOf(actionId)});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (cursor!=null&&cursor.getCount()>0){
                    YSFile ysFile=new YSFile(YSFile.EXT_TF);
                    ysFile.putHeader("file_type","package");
                    ysFile.putHeader("action", String.valueOf(actionId));
//                    ysFile.putHeader("agent_id","current");
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date=new Date();
                    ysFile.putHeader("date",dateFormat.format(date));

                    StringBuilder builder=new StringBuilder();
                    while (cursor.moveToNext()){
                        builder.append(cursor.getString(1));
                        if (cursor.isLast()){
                            maxIndex=cursor.getInt(0);
                            SQLiteManager.getInstance().savePathLastId(actionId,maxIndex);
                        }else {
                            builder.append(",");
                        }
                    }
                    ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));

                    dataBaseHelper.close();

                    try {

                        String folderName = android.os.Environment.getExternalStorageDirectory() +
                                Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
                        File path_task_folder = new File(folderName);
                        if (!path_task_folder.exists())
                            path_task_folder.mkdirs();

                        StringBuilder fileNameBuilder=new StringBuilder("Path_");
                        fileNameBuilder.append(DeviceManager.getInstance().getDeviceId());
                        fileNameBuilder.append("_");
                        fileNameBuilder.append(FileManager.getInstance().getPathFileLastIndex() + 1);
                        fileNameBuilder.append(".tf");

                        File file=new File(path_task_folder,fileNameBuilder.toString());
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        bos.write(ysFile.toBytes());
                        bos.flush();
                        bos.close();
                        fos.close();

                        FileManager.getInstance().savePathFileIndex(FileManager.getInstance().getPathFileLastIndex() + 1);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            while (cursor!=null&&cursor.getCount()==1000);
        }



    }

}
