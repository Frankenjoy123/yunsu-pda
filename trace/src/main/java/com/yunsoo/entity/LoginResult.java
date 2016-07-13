package com.yunsoo.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Frank Zhou on 2015/6/29
/ */
public class LoginResult implements JSONEntity{
    private String accessToken;

    private String permanentToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getPermanentToken() {
        return permanentToken;
    }

    @Override
    public String toJsonString() throws JSONException {
        return null;
    }

    @Override
    public void populate(JSONObject object)  {
        JSONObject object1=object.optJSONObject("access_token");
        accessToken=object1.optString("token");

        JSONObject object2=object.optJSONObject("permanent_token");
        permanentToken=object2.optString("token");
    }

}
