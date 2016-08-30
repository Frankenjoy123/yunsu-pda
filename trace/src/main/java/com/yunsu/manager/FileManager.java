package com.yunsu.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsu.common.util.Constants;
import com.yunsu.common.manager.BaseManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank zhou on 2015/7/15.
 */
public class FileManager extends BaseManager {

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

    public long getAllCacheSize(){
        String pathSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_SUCCESS_FOLDER;
        File pathSuccessFile = new File(pathSuccessFolderName);
        String LogSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_SYNC_FOLDER;
        File pathLogSuccessFile = new File(pathSuccessFolderName);
        return getFileLength(pathLogSuccessFile)+getFileLength(pathLogSuccessFile);
    }

    private long getFileLength(File dir) {
        if (dir == null || !dir.exists())
            return 0;
        String[] children = dir.list();
        long lDirSize = 0;
        for (int i = 0; i < children.length; i++) {
            File childFile = new File(dir, children[i]);
            if (childFile.isDirectory())
                lDirSize += getFileLength(childFile);
            else
                lDirSize += childFile.length();
        }
        return lDirSize;
    }

    // Clear all the related caches
    public void clearCache() {
        String pathSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_SUCCESS_FOLDER;
        File pathSuccessFile = new File(pathSuccessFolderName);
        String LogSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_SYNC_FOLDER;
        File pathLogSuccessFile = new File(LogSuccessFolderName);
        if (pathSuccessFile.exists()&&pathSuccessFile.isDirectory()){
            String[] children = pathSuccessFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(pathSuccessFile, children[i]).delete();
            }
        }
        if (pathLogSuccessFile.exists()&&pathLogSuccessFile.isDirectory()){
            String[] children = pathLogSuccessFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(pathLogSuccessFile, children[i]).delete();
            }
        }
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

    public List<String> getUnSyncLogFileNames() {
        List<String> fileNames = null;
        try {
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_LOG_NOT_SYNC_FOLDER;
            File pack_task_folder = new File(folderName);
            if (pack_task_folder.exists()){
                String[] packFiles= pack_task_folder.list();
                fileNames=new ArrayList<>();
                if (packFiles!=null&&packFiles.length>0){
                    for (int i=0;i<packFiles.length;i++){
                        fileNames.add(packFiles[i]);
                    }
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
