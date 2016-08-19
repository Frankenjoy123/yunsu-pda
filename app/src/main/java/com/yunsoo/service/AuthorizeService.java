package com.yunsu.service;

import android.util.Log;

import com.yunsu.entity.AuthUser;
import com.yunsu.entity.AuthorizeRequest;
import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;
import com.yunsu.manager.SessionManager;
import com.yunsu.network.RequestManager;

import org.json.JSONObject;

public class AuthorizeService extends DataServiceImpl {
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

    private static final String LOGIN_URL = "/device/register";
    private AuthorizeRequest request;

    public AuthorizeService(AuthorizeRequest request){
        this.request=request;
    }


    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {
            Log.d("ZXW","AuthorizeService method");
            JSONObject object=RequestManager.Post(LOGIN_URL,request.toJsonString());
            return object;
    }

}
