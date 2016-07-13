package com.yunsoo.service;

import android.util.Log;

import com.yunsoo.entity.AuthUser;
import com.yunsoo.entity.AuthorizeRequest;
import com.yunsoo.exception.LocalGeneralException;
import com.yunsoo.exception.NetworkNotAvailableException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.exception.ServerGeneralException;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.network.RequestManager;

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
