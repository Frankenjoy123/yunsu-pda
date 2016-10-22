package com.yunsu.common.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.network.RequestManager;
import com.yunsu.common.util.Constants;

import org.json.JSONObject;

public class PermanentTokenLoginService extends DataServiceImpl {
//GET http://api.test.yunsu.co:6080/auth/accesstoken?permanent_token=00f6ad1ccfe28e847bf5ee3c88a316cb6a2d0012
    private static final String URL = "/auth/accesstoken?permanent_token=";

    private String permanent_token;

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public PermanentTokenLoginService(String permanent_token) {
        this.permanent_token = permanent_token;
    }

    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {

        Log.d("ZXW","PermanentTokenLoginService start");
        JSONObject data = RequestManager.GetWithURL(URL + permanent_token);
        String newAccessToken=data.optString("token");

        AuthUser authUser=SessionManager.getInstance().getAuthUser();
        AuthUser tempAuthUser=new AuthUser(authUser);
        tempAuthUser.setAccessToken(newAccessToken);
        SessionManager.getInstance().saveLoginCredential(tempAuthUser);

        if (context!=null){
            SharedPreferences preferences=context.getSharedPreferences(Constants.Preference.YUNSU_PDA,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean(Constants.Preference.IS_AUTHORIZE, true);
            editor.commit();
        }

        return data;
    }


}
