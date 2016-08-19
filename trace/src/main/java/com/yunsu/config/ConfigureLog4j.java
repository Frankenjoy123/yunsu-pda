package com.yunsu.config;

import android.os.Environment;

import com.yunsu.common.util.Constants;

import org.apache.log4j.Level;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by yunsu on 2016/8/19.
 */
public class ConfigureLog4j  {
    private static boolean configured = false;

    public static void configure() {
        if (configured == true) {
            return;
        }

        String status = Environment.getExternalStorageState();

        final LogConfigurator logConfigurator = new LogConfigurator();

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            //设置是否启用文件附加,默认为true。false为覆盖文件
            logConfigurator.setUseFileAppender(true);
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            StringBuilder builder=new StringBuilder();
            builder.append(dateFormat.format(new Date()));
            builder.append(".log");
            logConfigurator.setFileName(Environment.getExternalStorageDirectory()+ Constants.YUNSOO_FOLDERNAME
                    + Constants.PATH_LOG_NOT_SYNC_FOLDER+ File.separator + builder.toString());
        } else {
            logConfigurator.setUseFileAppender(false);
        }


        //设置root日志输出级别 默认为DEBUG
        logConfigurator.setRootLevel(Level.ERROR);
        // 设置日志输出级别
        logConfigurator.setLevel("com.yunsu", Level.ERROR);
        //设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        //设置输出到控制台的文字格式 默认%m%n
        logConfigurator.setLogCatPattern("%m%n");
        //设置总文件大小
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        //设置最大产生的文件个数
        logConfigurator.setMaxBackupSize(1);
        //设置所有消息是否被立刻输出 默认为true,false 不输出
        logConfigurator.setImmediateFlush(true);
        //是否本地控制台打印输出 默认为true ，false不输出
        logConfigurator.setUseLogCatAppender(true);
        //设置是否重置配置文件，默认为true
        logConfigurator.setResetConfiguration(true);
        //是否显示内部初始化日志,默认为false
        logConfigurator.setInternalDebugging(false);

        logConfigurator.configure();

        configured = true;
    }
}
