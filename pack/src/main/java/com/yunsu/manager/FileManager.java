package com.yunsu.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsu.activity.R;
import com.yunsu.common.manager.BaseManager;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.YSFile;
import com.yunsu.entity.PackProductsEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    public List<String> getPackFileNames() {
        List<String> fileNames = null;
        try {
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_TASK_FOLDER;
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

    public long getAllCacheSize(){
        String pathSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_SUCCESS_FOLDER;
        File pathSuccessFile = new File(pathSuccessFolderName);
        String LogSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PACK_LOG_SYNC_FOLDER;
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
                Constants.YUNSOO_FOLDERNAME+Constants.PACK_SYNC_SUCCESS_FOLDER;
        File pathSuccessFile = new File(pathSuccessFolderName);
        String LogSuccessFolderName = android.os.Environment.getExternalStorageDirectory() +
                Constants.YUNSOO_FOLDERNAME+Constants.PACK_LOG_SYNC_FOLDER;
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

    /*
file_type: package
org_id: {id(19)}
product_base_id: {id(19)}
package_count: {int}
package_size: {int 规格}
product_count: {int}
factory: {string(100) 工厂}
workshop: {string(100) 车间}
production_line: {string(100) 生产线}
address: {string(255)}
details: {? 待定}
datetime: 2016-07-20T14:20:30.123Z
  */
    public String createPackFile(List<PackProductsEntity> packProductsEntityList) {

        PackService packService=new PackServiceImpl();
        if (packProductsEntityList!=null&&packProductsEntityList.size()>0) {
            int productCount=0;
            StringBuilder builder = new StringBuilder();
            for (PackProductsEntity entity : packProductsEntityList) {
                builder.append(entity.getPack().getLastSaveTime());
                builder.append(Constants.BLANK);
                builder.append(entity.getPack().getPackKey());
                builder.append(":");
                builder.append(entity.getProductsString());
                builder.append("\r\n");
                productCount+=entity.getPack().getRealCount();
            }

            YSFile ysFile = new YSFile(YSFile.EXT_TF);
            ysFile.putHeader("file_type", "package");
            ysFile.putHeader("org_id", SessionManager.getInstance().getAuthUser().getOrgId());
            ysFile.putHeader("package_count",String.valueOf(packProductsEntityList.size()));
            ysFile.putHeader("package_size",String.valueOf(packProductsEntityList.get(0).getPack().getStandard()));
            ysFile.putHeader("product_count",String.valueOf(productCount));
            ysFile.putHeader("factory","氧泡泡工厂");
            ysFile.putHeader("workshop","第一车间");
            ysFile.putHeader("production_line","第一生产线");
            ysFile.putHeader("address","杭州");
            ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));

            try {

                String folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME + Constants.PACK_SYNC_TASK_FOLDER;
                File pack_task_folder = new File(folderName);
                if (!pack_task_folder.exists())
                    pack_task_folder.mkdirs();

                File file = new File(pack_task_folder, generateFileName(date));

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(ysFile.toBytes());
                bos.flush();
                bos.close();
                fos.close();

                return file.getAbsolutePath();


//                FileManager.getInstance().savePackFileIndex(FileManager.getInstance().getPackFileLastIndex() + 1);

//                for (Pack pack : queryPackList){
//                    pack.setStatus(Constants.DB.SYNC);
//                    SimpleDateFormat format=new SimpleDateFormat(Constants.dateFormat);
//                    pack.setLastSaveTime(format.format(new Date()));
//                    packService.updatePack(pack);
//                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void deleteRowInPackFile(String fileName, String packKey) throws IOException {
        if (!StringHelper.isStringNullOrEmpty(fileName) && !StringHelper.isStringNullOrEmpty(packKey)){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME + Constants.PACK_SYNC_TASK_FOLDER;
            File file = new File(folderName,fileName);

            if (file.exists()){

                StringBuffer stringBuffer=new StringBuffer(4096);
                FileReader fileReader= null;
                BufferedReader bufferedReader=null;
                try {
                    fileReader = new FileReader(file);
                    bufferedReader=new BufferedReader(fileReader);
                    String temp;
                    while ((temp=bufferedReader.readLine())!=null){
                        if (!temp.contains(packKey)){
                            stringBuffer.append(temp);
                            stringBuffer.append("\r\n");
                        }
                    }
                } catch (FileNotFoundException e) {
                    String showMessage=String.format(this.context.getString(R.string.file_not_found),fileName);
                    throw new FileNotFoundException(showMessage);
                } catch (IOException e) {
                    throw new IOException("读取文件"+fileName+"错误");
                }

                FileWriter writer=null;
                    BufferedWriter bufferedWriter=null;
                    try {
                        writer=new FileWriter(file,false);
                        bufferedWriter=new BufferedWriter(writer);
                        bufferedWriter.append(stringBuffer.toString());
                        bufferedWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedWriter!=null){
                            try {
                                bufferedWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (writer!=null){
                            try {
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            }else {
                String showMessage=String.format(this.context.getString(R.string.file_not_found),fileName);
                throw new FileNotFoundException(showMessage);
            }

        }
    }


    public void writePackInfoToFile(String packKey ,List<String> productKeyList) {

        String string=createPackProductItemString(packKey,productKeyList);
        if (string !=null){
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME + Constants.PACK_SYNC_TASK_FOLDER;
            File pack_task_folder = new File(folderName);
            if (!pack_task_folder.exists())
                pack_task_folder.mkdirs();

            File file = new File(pack_task_folder, generateFileName(new Date()));
            FileWriter writer=null;
            BufferedWriter bufferedWriter=null;
            try {
                 writer=new FileWriter(file,true);
                bufferedWriter=new BufferedWriter(writer);
                bufferedWriter.append(string);
                bufferedWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedWriter!=null){
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (writer!=null){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public String generateFileName(Date date){
        SimpleDateFormat format =new SimpleDateFormat(Constants.dateOnlyDayFormat);

        String deviceId=DeviceManager.getInstance().getDeviceId();
        String tempId=null;

        if (deviceId.length()>6){
            tempId=deviceId.substring(deviceId.length()-6,deviceId.length());
        }else {
            tempId=deviceId;
        }

        StringBuilder fileNameBuilder=new StringBuilder(tempId);
        fileNameBuilder.append("-");
        fileNameBuilder.append(format.format(date)+".txt");
        return fileNameBuilder.toString();
    }

    public String generateFileName(String date){
        String deviceId=DeviceManager.getInstance().getDeviceId();
        String tempId=null;

        if (deviceId.length()>6){
            tempId=deviceId.substring(deviceId.length()-6,deviceId.length());
        }else {
            tempId=deviceId;
        }

        StringBuilder fileNameBuilder=new StringBuilder(tempId);
        fileNameBuilder.append("-");
        fileNameBuilder.append(date+".txt");
        return fileNameBuilder.toString();
    }



    private String createPackProductItemString(String packKey,List<String> productKeyList){
        if (!StringHelper.isStringNullOrEmpty(packKey) && productKeyList!=null && productKeyList.size() >0){
//            SimpleDateFormat format=new SimpleDateFormat(Constants.dateFormat);
            StringBuilder builder=new StringBuilder();
            builder.append(packKey);
            builder.append(":");
            for (int i = 0; i < productKeyList.size(); i ++) {
                builder.append(productKeyList.get(i));
                if (i < productKeyList.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("\r\n");
            return builder.toString();
        }

        return  null;
    }


    private void createSyncInboundPackFile(List<Pack> queryPackList) {
        boolean autoInbound= SettingManager.getInstance().isAutoInbound();
        if (autoInbound && queryPackList!=null&&queryPackList.size()>0) {
            StringBuilder builder = new StringBuilder();
            for (Pack pack : queryPackList) {
                builder.append(pack.getPackKey());
                builder.append("\r\n");
            }

            YSFile ysFile = new YSFile(YSFile.EXT_TF);
            ysFile.putHeader("file_type", "trace");
            ysFile.putHeader("org_id", SessionManager.getInstance().getAuthUser().getOrgId());
            ysFile.putHeader("package_count", String.valueOf(queryPackList.size()));
            ysFile.putHeader("action", "inbound");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            ysFile.putHeader("date", dateFormat.format(new Date()));
            ysFile.putHeader("source", "storage_name");
            ysFile.putHeader("storage_name", "default");

            ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));

            try {

                String folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME + Constants.PACK_AUTO_INBOUND_TASK_FOLDER;
                File pack_task_folder = new File(folderName);
                if (!pack_task_folder.exists())
                    pack_task_folder.mkdirs();

                StringBuilder fileNameBuilder = new StringBuilder("Pack_Auto_Inbound");
                fileNameBuilder.append(DeviceManager.getInstance().getDeviceId());
                fileNameBuilder.append("_");
                fileNameBuilder.append(FileManager.getInstance().getPackFileLastIndex() + 1);
                fileNameBuilder.append(".tf");

                File file = new File(pack_task_folder, fileNameBuilder.toString());
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(ysFile.toBytes());
                bos.flush();
                bos.close();
                fos.close();
                FileManager.getInstance().savePackFileIndex(FileManager.getInstance().getPackFileLastIndex() + 1);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void savePackFileIndex(int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.Preference.PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.PACK_FILE_LAST_INDEX, index);
        editor.commit();
    }

    public List<String> getUnSyncLogFileNames() {
        List<String> fileNames = null;
        try {
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PACK_LOG_NOT_SYNC_FOLDER;
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
