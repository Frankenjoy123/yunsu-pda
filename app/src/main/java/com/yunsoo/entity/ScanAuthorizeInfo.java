package com.yunsoo.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanAuthorizeInfo implements JSONEntity {
    private String accountId;
    private String token;
    private String deviceName;
    private String deviceComments;

    public String getAccountId() {
        return accountId;
    }

    public String getToken() {
        return token;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceComments() {
        return deviceComments;
    }

    @Override
	public String toJsonString() throws JSONException {
		return new JSONObject().toString();
	}

	@Override
	public void populate(JSONObject object) {
		accountId=object.optString("a");
        token=object.optString("t");
        deviceName=object.optString("dn");
        deviceComments=object.optString("dc");
	}

}
