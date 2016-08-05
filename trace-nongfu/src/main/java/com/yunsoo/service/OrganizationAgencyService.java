package com.yunsoo.service;

import android.util.Log;

import com.yunsoo.exception.LocalGeneralException;
import com.yunsoo.exception.NetworkNotAvailableException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.exception.ServerGeneralException;
import com.yunsoo.manager.LogisticManager;
import com.yunsoo.network.RequestManager;

import org.json.JSONObject;

public class OrganizationAgencyService extends DataServiceImpl {
//    GET http://api.test.yunsu.co:6080/logisticsaction?orgId=current
    private static final String URL = "/organizationAgency";


    @Override
    protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception {

        Log.d("ZXW","OrganizationAgencyService start");
        JSONObject result = RequestManager.GetWithURL(URL);

        LogisticManager.getInstance().saveOrganizationAgency(result);

        return result;

    }

}
