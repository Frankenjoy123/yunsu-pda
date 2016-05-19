package com.yunsoo.service;

import android.os.Environment;
import android.util.Log;

import com.yunsoo.entity.AuthUser;
import com.yunsoo.exception.LocalGeneralException;
import com.yunsoo.exception.NetworkNotAvailableException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.exception.ServerGeneralException;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.network.RequestManager;
import com.yunsoo.util.Constants;

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
        if (fileType.equals(FileUpLoadService.PACK_FILE)){
            url="/package/file";
        }else if (fileType.equals(FileUpLoadService.PATH_FILE)){
            url="/logistics/file";
        }
        return RequestManager.PostByFile(url,filePath);

    }
}
