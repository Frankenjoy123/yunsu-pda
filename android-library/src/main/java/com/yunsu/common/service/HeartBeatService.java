package com.yunsu.common.service;

import android.util.Log;

import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.common.network.RequestManager;


import org.json.JSONObject;

public class HeartBeatService extends DataServiceImpl {

    private static final String URL = "/auth/heartbeat";

    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {
        Log.d("ZXW","HeartBeatService method");
        JSONObject object= RequestManager.Post(URL,"{}");
        return object;
    }

}
