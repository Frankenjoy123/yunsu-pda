package com.yunsu.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsu.activity.R;
import com.yunsu.common.manager.BaseManager;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.manager.FileLocationManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.YSFile;
import com.yunsu.entity.PackProductsEntity;
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
    private static final String TAG = FileManager.class.getSimpleName();

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
            String folderName = FileLocationManager.getInstance().getDataTaskFolder();
            File pack_task_folder = new File(folderName);
            String[] packFiles = pack_task_folder.list();
            fileNames = new ArrayList<>();
            if (packFiles != null && packFiles.length > 0) {
                for (int i = 0; i < packFiles.length; i++) {
                    fileNames.add(packFiles[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
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
    public String createPackFile(List<PackProductsEntity> packProductsEntityList, String date) {

        String filePath = null;

        PackService packService = new PackServiceImpl();
        if (packProductsEntityList != null && packProductsEntityList.size() > 0) {
            int productCount = 0;
            StringBuilder builder = new StringBuilder();
            for (PackProductsEntity entity : packProductsEntityList) {
                builder.append(entity.getPack().getLastSaveTime());
                builder.append(Constants.BLANK);
                builder.append(entity.getPack().getPackKey());
                builder.append(":");
                builder.append(entity.getProductsString());
                builder.append("\r\n");
                productCount += entity.getPack().getRealCount();
            }

            YSFile ysFile = new YSFile(YSFile.EXT_TF);
            ysFile.putHeader("file_type", "package");
            ysFile.putHeader("org_id", SessionManager.getInstance().getAuthUser().getOrgId());
            ysFile.putHeader("package_count", String.valueOf(packProductsEntityList.size()));
            ysFile.putHeader("package_size", String.valueOf(packProductsEntityList.get(0).getPack().getStandard()));
            ysFile.putHeader("product_count", String.valueOf(productCount));
            ysFile.putHeader("factory", "氧泡泡工厂");
            ysFile.putHeader("workshop", "第一车间");
            ysFile.putHeader("production_line", "第一生产线");
            ysFile.putHeader("address", "上虞");
            ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));

            try {

                String folderName = FileLocationManager.getInstance().getDataTaskFolder();
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

                filePath = file.getAbsolutePath();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return filePath;

    }

    public void createPackFileOffline(PackProductsEntity entity){
        if (entity!=null ){
            StringBuilder builder = new StringBuilder();
            builder.append(entity.getPack().getPackKey());
            builder.append(":");
            builder.append(entity.getProductsString());
            builder.append("\r\n");

            try {

                String folderName = FileLocationManager.getInstance().getDataTaskFolder();
                File pack_task_folder = new File(folderName);
                if (!pack_task_folder.exists())
                    pack_task_folder.mkdirs();

                File file = new File(pack_task_folder, generateFileName(new Date()));
                FileWriter writer=null;
                BufferedWriter bufferedWriter=null;
                try {
                    writer=new FileWriter(file,true);
                    bufferedWriter=new BufferedWriter(writer);
                    bufferedWriter.append(builder.toString());
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

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


    }

    public void deleteRowInPackFile(String fileName, String packKey) throws IOException {
        if (!StringHelper.isStringNullOrEmpty(fileName) && !StringHelper.isStringNullOrEmpty(packKey)) {
            String folderName = FileLocationManager.getInstance().getDataTaskFolder();
            File file = new File(folderName, fileName);

            if (file.exists()) {

                StringBuffer stringBuffer = new StringBuffer(4096);
                FileReader fileReader = null;
                BufferedReader bufferedReader = null;
                try {
                    fileReader = new FileReader(file);
                    bufferedReader = new BufferedReader(fileReader);
                    String temp;
                    while ((temp = bufferedReader.readLine()) != null) {
                        if (!temp.contains(packKey)) {
                            stringBuffer.append(temp);
                            stringBuffer.append("\r\n");
                        }
                    }
                } catch (FileNotFoundException e) {
                    String showMessage = String.format(this.context.getString(R.string.file_not_found), fileName);
                    throw new FileNotFoundException(showMessage);
                } catch (IOException e) {
                    throw new IOException("读取文件" + fileName + "错误");
                }

                FileWriter writer = null;
                BufferedWriter bufferedWriter = null;
                try {
                    writer = new FileWriter(file, false);
                    bufferedWriter = new BufferedWriter(writer);
                    bufferedWriter.append(stringBuffer.toString());
                    bufferedWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                String showMessage = String.format(this.context.getString(R.string.file_not_found), fileName);
                throw new FileNotFoundException(showMessage);
            }

        }
    }


    public void writePackInfoToFile(String packKey, List<String> productKeyList) {

        String string = createPackProductItemString(packKey, productKeyList);
        if (string != null) {
            String folderName = FileLocationManager.getInstance().getDataTaskFolder();
            File pack_task_folder = new File(folderName);
            if (!pack_task_folder.exists())
                pack_task_folder.mkdirs();

            File file = new File(pack_task_folder, generateFileName(new Date()));
            FileWriter writer = null;
            BufferedWriter bufferedWriter = null;
            try {
                writer = new FileWriter(file, true);
                bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.append(string);
                bufferedWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public String generateFileName(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(Constants.dateOnlyDayFormat);

        String deviceId = DeviceManager.getInstance().getDeviceId();
        String tempId = null;

        if (deviceId.length() > 6) {
            tempId = deviceId.substring(deviceId.length() - 6, deviceId.length());
        } else {
            tempId = deviceId;
        }

        StringBuilder fileNameBuilder = new StringBuilder(tempId);
        fileNameBuilder.append("-");
        fileNameBuilder.append(format.format(date) + ".txt");
        return fileNameBuilder.toString();
    }

    public String generateFileName(String date) {
        String deviceId = DeviceManager.getInstance().getDeviceId();
        String tempId = null;

        if (deviceId.length() > 6) {
            tempId = deviceId.substring(deviceId.length() - 6, deviceId.length());
        } else {
            tempId = deviceId;
        }

        StringBuilder fileNameBuilder = new StringBuilder(tempId);
        fileNameBuilder.append("-");
        fileNameBuilder.append(date + ".txt");
        return fileNameBuilder.toString();
    }


    private String createPackProductItemString(String packKey, List<String> productKeyList) {
        if (!StringHelper.isStringNullOrEmpty(packKey) && productKeyList != null && productKeyList.size() > 0) {
//            SimpleDateFormat format=new SimpleDateFormat(Constants.dateFormat);
            StringBuilder builder = new StringBuilder();
            builder.append(packKey);
            builder.append(":");
            for (int i = 0; i < productKeyList.size(); i++) {
                builder.append(productKeyList.get(i));
                if (i < productKeyList.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("\r\n");
            return builder.toString();
        }

        return null;
    }


    public void savePackFileIndex(int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.Preference.PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.Preference.PACK_FILE_LAST_INDEX, index);
        editor.commit();
    }


    public int getPackFileLastIndex() {
        SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getInt(Constants.Preference.PACK_FILE_LAST_INDEX, 0);
    }


    public boolean isAllFileUpload(List<Integer> status) {
        for (int i : status) {
            if (i != 2) {
                return false;
            }
        }
        return true;
    }
}
