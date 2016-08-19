package com.yunsu.service;

import android.os.Environment;
import android.util.Log;

import com.yunsu.entity.AuthUser;
import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;
import com.yunsu.manager.SessionManager;
import com.yunsu.network.RequestManager;
import com.yunsu.util.Constants;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;


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
            url="/taskfile?file_name="+fileName;
        }else {
            url="/taskfile";
        }
         return  RequestManager.PostByFile(url,filePath);
    }
}
