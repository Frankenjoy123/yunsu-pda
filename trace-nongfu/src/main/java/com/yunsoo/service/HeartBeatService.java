package com.yunsoo.service;

import android.util.Log;

import com.yunsoo.exception.LocalGeneralException;
import com.yunsoo.exception.NetworkNotAvailableException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.exception.ServerGeneralException;
import com.yunsoo.network.RequestManager;

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
