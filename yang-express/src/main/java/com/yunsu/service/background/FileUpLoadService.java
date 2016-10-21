package com.yunsu.service.background;

import android.util.Log;

import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.entity.PackProductsEntity;

import org.json.JSONObject;

import java.util.List;


public class FileUpLoadService extends DataServiceImpl {
    private String filePath;
    private String url;

    private List<PackProductsEntity> packProductsEntityList;

    public List<PackProductsEntity> getPackProductsEntityList() {
        return packProductsEntityList;
    }

    public void setPackProductsEntityList(List<PackProductsEntity> packProductsEntityList) {
        this.packProductsEntityList = packProductsEntityList;
    }

    public FileUpLoadService(String filePath)
    {
        this.filePath = filePath;
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
