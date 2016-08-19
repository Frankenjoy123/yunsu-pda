package com.yunsu.service;

import android.util.Log;

import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;
import com.yunsu.network.RequestManager;

import org.json.JSONObject;

public class HeartBeatService extends DataServiceImpl {

    private static final String URL = "/auth/heartbeat";

    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {
        Log.d("ZXW","HeartBeatService method");
        JSONObject object=RequestManager.Post(URL,"{}");
        return object;
    }

}
