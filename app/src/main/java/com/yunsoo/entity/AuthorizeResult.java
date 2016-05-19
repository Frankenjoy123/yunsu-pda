package com.yunsoo.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Zhou on 2015/6/29
/ */
public class AuthorizeResult implements JSONEntity{
    private String deviceId;
    private String orgId;
    private String loginAccountId;
    private String createdAccountId;
    private String modifiedAccountId;
    private String deviceName;
    private String statusCode;
    private String deviceComments;

    public String getDeviceId() {
        return deviceId;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getLoginAccountId() {
        return loginAccountId;
    }

    public String getCreatedAccountId() {
        return createdAccountId;
    }

    public String getModifiedAccountId() {
        return modifiedAccountId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getDeviceComments() {
        return deviceComments;
    }

    @Override
    public String toJsonString() throws JSONException {
        return null;
    }

    @Override
    public void populate(JSONObject object)  {

        deviceId=object.optString("id");
        orgId=object.optString("org_id");
        loginAccountId=object.optString("login_account_id");
        deviceName=object.optString("name");
        statusCode=object.optString("status_code");
        deviceComments=object.optString("comments");
        createdAccountId=object.optString("created_account_id");
        modifiedAccountId=object.optString("modified_account_id");
    }




}
