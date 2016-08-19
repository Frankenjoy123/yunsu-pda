package com.yunsu.service;

import android.util.Log;

import com.yunsu.entity.AuthUser;
import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;
import com.yunsu.manager.SessionManager;
import com.yunsu.network.RequestManager;

import org.json.JSONObject;

public class PermanentTokenLoginService extends DataServiceImpl {
//GET http://api.test.yunsu.co:6080/auth/accesstoken?permanent_token=00f6ad1ccfe28e847bf5ee3c88a316cb6a2d0012
    private static final String URL = "/auth/accesstoken?permanent_token=";

    private String permanent_token;

    public PermanentTokenLoginService(String permanent_token) {
        this.permanent_token = permanent_token;

    }

    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {

        Log.d("ZXW","PermanentTokenLoginService start");
        JSONObject result = RequestManager.GetWithURL(URL + permanent_token);
        String accessToken=result.optString("token");

        AuthUser user = new AuthUser();
        user.setAccessToken(accessToken);

        SessionManager.getInstance().saveLoginCredential(user);


        return result;

    }

}
