package com.yunsoo.fileOpreation;


import android.text.format.Time;

import com.yunsoo.manager.DeviceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by Chen Jerry on 3/9/2015.
 */
public class FileOperation {

    private static final String FOLDERNAME = "/yunsoo";
//    public static String createPackFileName() {
//
//        String folderName = android.os.Environment.getExternalStorageDirectory() + FOLDERNAME+"/pack/sync_task";
//
//        File file = new File(folderName);
//        if (!file.exists())
//            file.mkdirs();
//
//        fileName = folderName + DeviceManager.getInstance().getDeviceId()+;
//
//        return fileName;
//    }

    public static String createNewFileName(String FILENAME) {
        String fileName;
        Time t = new Time();
        t.setToNow();

        int i_year = t.year;
        int i_month = t.month+1;
        int i_day = t.monthDay;
        int i_th = t.hour;
        int i_tm = t.minute;
        int i_ts = t.second;
        String ty = String.valueOf(i_year);
        String tmon = String.valueOf(i_month);
        String td = String.valueOf(i_day);
        String th = String.valueOf(i_th);
        String tm = String.valueOf(i_tm);
        String ts = String.valueOf(i_ts);

        if (i_th < 10)
            th = "0" + th;

        if (i_tm < 10)
            tm = "0" + tm;

        if (i_ts < 10)
            ts = "0" + ts;

        if (i_month < 10)
            tmon = "0" + tmon;

        if (i_day < 10)
            td = "0" + td;

        if (i_th < 10)
            fileName = FILENAME + ty + "_" + tmon + "_" + td + "_01.txt";
        else if (i_th < 14)
            fileName = FILENAME + ty + "_" + tmon + "_" + td + "_02.txt";
        else if (i_th < 18)
            fileName = FILENAME + ty + "_" + tmon + "_" + td + "_03.txt";
        else if (i_th < 24)
            fileName = FILENAME + ty + "_" + tmon + "_" + td + "_04.txt";
        else
            fileName = FILENAME + ty + "_" + tmon + "_" + td + "_00.txt";

        String folderName = android.os.Environment.getExternalStorageDirectory() + FOLDERNAME;

        File file = new File(folderName);
        if (!file.exists())
            file.mkdirs();

        fileName = folderName + fileName;

        return fileName;
    }


    public static void replaceTxtByStr(String oldStr,String replaceStr,File file) {
         String temp = "";
         try {
//             File file = new File(path);
             FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr);
             StringBuffer buf = new StringBuffer();

             // 保存该行前面的内容
             for (int j = 1; (temp = br.readLine()) != null
             && !temp.equals(oldStr); j++) {
                 buf = buf.append(temp);
                 buf = buf.append(System.getProperty("line.separator"));
             }

             // 将内容插入
             buf = buf.append(replaceStr);

             // 保存该行后面的内容
             while ((temp = br.readLine()) != null) {
             buf = buf.append(System.getProperty("line.separator"));
             buf = buf.append(temp);
             }

             br.close();
             FileOutputStream fos = new FileOutputStream(file);
             PrintWriter pw = new PrintWriter(fos);
             pw.write(buf.toString().toCharArray());
             pw.flush();
             pw.close();
         } catch (IOException e) {
         e.printStackTrace();
         }
     }


}
