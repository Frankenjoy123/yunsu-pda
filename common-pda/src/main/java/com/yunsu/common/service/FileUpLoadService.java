package com.yunsu.common.service;

import android.util.Log;

import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;

import org.json.JSONObject;


public class FileUpLoadService extends DataServiceImpl {
    public static final String PACK_FILE="pack_file";
    public static final String PATH_FILE="path_file";

    private String filePath;
    private int index;
    private String url;

    private String fileType;

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public FileUpLoadService(String filePath)
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
        Log.d("ZXW","FileUpLoadService start");
        String[] arrName=filePath.split("/");
        if (arrName.length>0){
            String fileName=arrName[arrName.length-1];
            url="/taskfile?file_name="+fileName + "&ignored=true";
        }else {
            url="/taskfile" + "&ignored=true";
        }
         return  RequestManager.PostByFile(url,filePath);
    }
}
