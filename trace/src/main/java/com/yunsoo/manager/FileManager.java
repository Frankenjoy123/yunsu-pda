package com.yunsoo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsoo.util.Constants;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank zhou on 2015/7/15.
 */
public class FileManager extends BaseManager{

    private static FileManager fileManager;
    private static final String TAG=FileManager.class.getSimpleName();
    public static FileManager initializeIntance(Context context) {

        if (fileManager == null) {
            synchronized (FileManager.class) {
                if (fileManager == null) {
                    fileManager = new FileManager();
                    fileManager.context = context;
                }
            }
        }
        return fileManager;
    }

    public static synchronized FileManager getInstance() {
        if (fileManager == null) {
            Log.d(TAG, "FileManager has not been initialized");
        }
        return fileManager;
    }

    public List<String> getPackFileNames() {
        List<String> fileNames = null;
        try {
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
            File pack_task_folder = new File(folderName);
            String[] packFiles= pack_task_folder.list();
            fileNames=new ArrayList<>();
            if (packFiles!=null&&packFiles.length>0){
                for (int i=0;i<packFiles.length;i++){
                    fileNames.add(packFiles[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public void savePackFileIndex(int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.Preference.PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.PACK_FILE_LAST_INDEX, index);
        editor.commit();
    }

    public int getPackFileLastIndex(){
        SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getInt(Constants.Preference.PACK_FILE_LAST_INDEX, 0);
    }

    public void savePathFileIndex(int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.Preference.PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.PATH_FILE_LAST_INDEX, index);
        editor.commit();
    }

    public int getPathFileLastIndex(){
        SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getInt(Constants.Preference.PATH_FILE_LAST_INDEX, 0);
    }

    public boolean isAllFileUpload(List<Integer> status){
        for (int i: status){
            if (i!=2){
                return false;
            }
        }
        return true;
    }
}
