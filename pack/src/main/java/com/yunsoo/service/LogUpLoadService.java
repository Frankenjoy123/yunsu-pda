package com.yunsu.service;

import android.util.Log;

import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;
import com.yunsu.network.RequestManager;

import org.json.JSONObject;


public class LogUpLoadService extends DataServiceImpl {
    private String filePath;
    private int index;
    private String url;

    public LogUpLoadService(String filePath)
    {
        this.filePath = filePath;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {
        Log.d("ZXW","LogUpLoadService start");
        String[] arrName=filePath.split("/");
        if (arrName.length>0){
            String fileName=arrName[arrName.length-1];
            url="/device/log?file_name="+fileName;
        }else {
            url="/device/log";
        }
         return  RequestManager.PostByFile(url,filePath);
    }
}
