package com.yunsoo.entity;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONEntity {
	public String toJsonString() throws JSONException;

	public void populate(JSONObject object);

}
