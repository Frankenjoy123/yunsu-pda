package com.yunsu.common.service;

import android.util.Log;


import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;

import org.json.JSONObject;

public class AuthLoginService extends DataServiceImpl {
    /**
     * http://api.test.yunsu.co:6080/auth/login/token
     * X-YS-AppId:34
     }
     */

    private static final String LOGIN_URL = "/auth/login/token";
    private String token;

    public AuthLoginService(String token){
        this.token=token;
    }



    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {
        Log.d("ZXW","AuthLoginService method");
        JSONObject request=new JSONObject();
        request.put("token",token);

        JSONObject object= RequestManager.Post(LOGIN_URL,request.toString());
        return object;
    }

}
