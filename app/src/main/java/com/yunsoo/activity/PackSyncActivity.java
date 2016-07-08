package com.yunsoo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsoo.adapter.FileSyncAdapter;
import com.yunsoo.exception.BaseException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.manager.DeviceManager;
import com.yunsoo.manager.FileManager;
import com.yunsoo.manager.SQLiteManager;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.service.DataServiceImpl;
import com.yunsoo.service.FileUpLoadService;
import com.yunsoo.service.PermanentTokenLoginService;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.util.Constants;
import com.yunsoo.util.YSFile;
import com.yunsoo.view.TitleBar;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PackSyncActivity extends BaseActivity implements DataServiceImpl.DataServiceDelegate{
    private MyDataBaseHelper dataBaseHelper;
    private ListView lv_pack_sync;
    private TitleBar titleBar;
    private TextView tv_empty_file_tip;
    private FileSyncAdapter adapter;

//    private int minIndex;
    private int maxIndex;

    private List<String> fileNames;
    private List<Integer> status;

    public static final String EXT_TF = "TF"; //task file
    private static final String VER_1_0 = "1.0";

    private FileOutputStream fos;
    private BufferedOutputStream bos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_sync);

        getActionBar().hide();

        init();

        createPackFile();

//        getPackFileNames();

    }


    private void init() {
        lv_pack_sync= (ListView) findViewById(R.id.lv_pack_sync);
        tv_empty_file_tip= (TextView) findViewById(R.id.tv_empty_file_tip);
        titleBar= (TitleBar) findViewById(R.id.pack_sync_title_bar);
        titleBar.setTitle(getString(R.string.pack_sync));
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setDisplayAsBack(true);
        titleBar.setRightButtonText(getString(R.string.sync));
        adapter=new FileSyncAdapter(this);
        fileNames=new ArrayList<>();
        status=new ArrayList<>();
        adapter.setFileNames(fileNames);
        adapter.setStatus(status);

        lv_pack_sync.setAdapter(adapter);


        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileNames!=null&&fileNames.size()>0 && !FileManager.getInstance().isAllFileUpload(status)){
                    String[] titleArray = new String[]{getString(R.string.off_line_upload),
                    getString(R.string.wifi_upload)};
                    AlertDialog dialog = new AlertDialog.Builder(PackSyncActivity.this)
                            .setItems(titleArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i==0){
                                        Intent intent=new Intent(PackSyncActivity.this,OffLineUploadActivity.class);
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
                Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_TASK_FOLDER;
        File pack_task_folder = new File(folderName);
        File[] files=pack_task_folder.listFiles();
        for(int i=0;i<files.length;i++){
            status.set(i,1);
            FileUpLoadService service=new FileUpLoadService(files[i].getAbsolutePath());
            service.setIndex(i);
            service.setDelegate(this);
            service.setFileType(FileUpLoadService.PACK_FILE);
            service.start();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestSucceeded(final DataServiceImpl service, JSONObject data, boolean isCached) {
        super.onRequestSucceeded(service, data, isCached);
        if (service instanceof FileUpLoadService){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_SUCCESS_FOLDER;
            File pack_success_folder = new File(folderName);
            if (!pack_success_folder.exists()){
                pack_success_folder.mkdirs();
            }
            File oldFile=new File(((FileUpLoadService) service).getFilePath());
            File newFile=new File(pack_success_folder,oldFile.getName());
            oldFile.renameTo(newFile);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    int index=((FileUpLoadService) service).getIndex();
                    status.set(index,2);
                    adapter.notifyDataSetChanged();
                    if (FileManager.getInstance().isAllFileUpload(status)){
                        hideLoading();
                    }
                }
            });
        }

        if (service instanceof PermanentTokenLoginService){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_TASK_FOLDER;
            File pack_task_folder = new File(folderName);
            File[] files=pack_task_folder.listFiles();
            for(int i=0;i<files.length;i++){
                status.set(i,1);
                FileUpLoadService fileUpLoadService=new FileUpLoadService(files[i].getAbsolutePath());
                fileUpLoadService.setFileType(FileUpLoadService.PACK_FILE);
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
                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_TASK_FOLDER;
            File pack_task_folder = new File(folderName);
            String[] packFiles= pack_task_folder.list();
            if (packFiles!=null&&packFiles.length>0){
                for (int i=0;i<packFiles.length;i++){
                    fileNames.add(packFiles[i]);
                    status.add(0);
                }
                lv_pack_sync.setEmptyView(tv_empty_file_tip);
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPackFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBaseHelper=new MyDataBaseHelper(PackSyncActivity.this, Constants.SQ_DATABASE,null,1);
                Cursor cursor= null;

                do {
                    try {
                        cursor = dataBaseHelper.getReadableDatabase().rawQuery("select * from pack where _id>? limit 1000",
                                new String[]{String.valueOf(SQLiteManager.getInstance().getPackLastId())});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (cursor!=null&&cursor.getCount()>0){
                        YSFile ysFile=new YSFile(YSFile.EXT_TF);
                        ysFile.putHeader("file_type","package");

                        StringBuilder builder=new StringBuilder();
                        while (cursor.moveToNext()){
                            if (cursor.isLast()){
                                maxIndex=cursor.getInt(0);
                                SQLiteManager.getInstance().savePackLastId(maxIndex);
                            }
                            builder.append(cursor.getString(3));
                            builder.append(Constants.BLANK);
                            builder.append(cursor.getString(1));
                            builder.append(":");
                            builder.append(cursor.getString(2));
                            builder.append("\r\n");
                        }
                        ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));
                        dataBaseHelper.close();


                        try{

                            String folderName = android.os.Environment.getExternalStorageDirectory() +
                                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_TASK_FOLDER;
                            File pack_task_folder = new File(folderName);
                            if (!pack_task_folder.exists())
                                pack_task_folder.mkdirs();

                            StringBuilder fileNameBuilder=new StringBuilder("Pack_");
                            fileNameBuilder.append(DeviceManager.getInstance().getDeviceId());
                            fileNameBuilder.append("_");
                            fileNameBuilder.append(FileManager.getInstance().getPackFileLastIndex() + 1);
                            fileNameBuilder.append(".tf");

                            File file=new File(pack_task_folder,fileNameBuilder.toString());

//                            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
//                            bw.write(bw);
//                            bw.flush();

                            fos = new FileOutputStream(file);
                            bos = new BufferedOutputStream(fos);
                            bos.write(ysFile.toBytes());
                            bos.flush();
                            FileManager.getInstance().savePackFileIndex(FileManager.getInstance().getPackFileLastIndex() + 1);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                while (cursor!=null&&cursor.getCount()==1000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getPackFileNames();
                    }
                });
            }
        }).start();

    }

}
