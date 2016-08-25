package com.yunsu.common.service;

import android.util.Log;


import com.yunsu.common.entity.AuthorizeRequest;
import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;

import org.json.JSONObject;

public class AuthorizeRegisterService extends DataServiceImpl {
    /**
     * Path：http://api.test.yunsu.co:6080/device/register
     Method：POST
     Input:
     {
     "id": "DEVICE-ID",
     "name": "test device 2",
     "comments": "test comments, please ignore.",
     "created_account_id": "2kadmvn8uh248k5k7wa",
     "modified_account_id": "2kadmvn8uh248k5k7wa"
     }
     */

    private static final String LOGIN_URL = "/auth/device/register";
    private AuthorizeRequest request;

    public AuthorizeRegisterService(AuthorizeRequest request){
        this.request=request;
    }


    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {
            Log.d("ZXW","AuthorizeRegisterService method");
            JSONObject object= RequestManager.Post(LOGIN_URL,request.toJsonString());
            return object;
    }

}
