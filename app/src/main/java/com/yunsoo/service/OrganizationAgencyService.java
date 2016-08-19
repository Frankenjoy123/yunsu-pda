package com.yunsu.service;

import android.util.Log;

import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;
import com.yunsu.manager.LogisticManager;
import com.yunsu.network.RequestManager;

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
