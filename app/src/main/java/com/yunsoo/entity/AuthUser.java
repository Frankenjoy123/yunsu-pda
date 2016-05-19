package com.yunsoo.entity;

import org.json.JSONException;
import org.json.JSONObject;



public class AuthUser implements JSONEntity {

    private String accessToken;

    private String permanentToken;

    private String api;

    public AuthUser() {
    }

    public AuthUser(AuthUser user) {
        this.accessToken = user.getAccessToken();
        this.permanentToken =user.getPermanentToken();
        this.api=user.getApi();
    }

    public void populate(JSONObject object) {
        accessToken = object.optString("accessToken");
        permanentToken =object.optString("permanentToken");
        api=object.optString("api");
    }

    public String toJsonString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("accessToken", accessToken);
        object.put("permanentToken", permanentToken);
        object.put("api", api);
        return object.toString();
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPermanentToken() {
        return permanentToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setPermanentToken(String permanentToken) {
        this.permanentToken = permanentToken;
    }
}
