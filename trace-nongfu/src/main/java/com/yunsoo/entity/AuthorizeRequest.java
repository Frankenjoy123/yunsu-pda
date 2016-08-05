package com.yunsoo.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthorizeRequest implements JSONEntity {

    private String deviceCode;
    private String deviceName;
    private String comments;
    private String accountId;
    private String os;

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Override
	public String toJsonString() throws JSONException {
		JSONObject object = new JSONObject();
        object.put("id",this.deviceCode);
        object.put("name", this.deviceName);
        object.put("comments", this.comments);
        object.put("created_account_id",this.accountId);
        object.put("os",this.os);
		return object.toString();
	}

	@Override
	public void populate(JSONObject object) {
		
	}

}
