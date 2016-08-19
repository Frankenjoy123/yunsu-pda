package com.yunsu.service;

import android.util.Log;

import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;
import com.yunsu.manager.LogisticManager;
import com.yunsu.common.network.RequestManager;
import com.yunsu.common.service.DataServiceImpl;

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
