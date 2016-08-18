package com.yunsoo.service;

import android.util.Log;

import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;
import com.yunsu.common.service.DataServiceImpl;

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
