package com.yunsu.service;

import android.util.Log;

import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.entity.PackProductsEntity;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.List;


public class FileUpLoadService extends DataServiceImpl {
    public static final String PACK_FILE="pack_file";
    public static final String PATH_FILE="path_file";

    public static final String IGNORED="ignored";
    public static final String FILE_NAME="file_name";
    public static final String COMMITTED="committed";

    public static final String TRUE="true";
    public static final String FALSE="false";

    private String filePath;
    private int index;
    private String url;

    private String fileType;


    List<BasicNameValuePair> queryPairList;

    List<PackProductsEntity> packProductsEntityList;

    public List<PackProductsEntity> getPackProductsEntityList() {
        return packProductsEntityList;
    }

    public void setPackProductsEntityList(List<PackProductsEntity> packProductsEntityList) {
        this.packProductsEntityList = packProductsEntityList;
    }

    public List<BasicNameValuePair> getQueryPairList() {
        return queryPairList;
    }

    public void setQueryPairList(List<BasicNameValuePair> queryPairList) {
        this.queryPairList = queryPairList;
    }

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
        url="/taskfile";
//        String[] arrName=filePath.split("/");
//        if (arrName.length>0){
//            String fileName=arrName[arrName.length-1];
//            url="/taskfile?file_name="+fileName + "&ignored=true";
//        }else {
//            url="/taskfile" + "&ignored=true";
//        }

         return  RequestManager.PostByFile(url,queryPairList,filePath);
    }
}
