package com.yunsoo.network;

import android.util.Log;

import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.exception.ServerGeneralException;
import com.yunsoo.manager.DeviceManager;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.util.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class RequestManager {

	private final static String TAG = RequestManager.class.getSimpleName();

	public static JSONObject Post(String url, String postJsonString) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url);
		restClient.SetIsJsonContent(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", postJsonString);

		jsonObject = restClient.Execute(RestClient.RequestMethod.POST);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}




    public static JSONObject GetWithURL(String url) throws ServerAuthException, ServerGeneralException, Exception {
        JSONObject jsonObject = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Log.d(TAG, "Request started: at " + format.format(new Date()));
        RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url);
        restClient.SetIsJsonContent(true);
        SetRequestHeader(restClient);
        jsonObject = restClient.Execute(RestClient.RequestMethod.GET);
        Log.d(TAG, "Request end: at " + format.format(new Date()));
        return HandleResponseResult(jsonObject);
    }

	public static JSONObject PostByFile(String url, String sFilePath) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url);
		restClient.SetIsJsonContent(false);
		restClient.setIsFilePost(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", sFilePath);

		jsonObject = restClient.Execute(RestClient.RequestMethod.POST);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject PUT(String url, String postJsonString) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url);
		restClient.SetIsJsonContent(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", postJsonString);

		jsonObject = restClient.Execute(RestClient.RequestMethod.PUT);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}



	public static JSONObject Get(String url, List<NameValuePair> nameValuePairs, Boolean... forceRefresh)
			throws ServerAuthException, ServerGeneralException, Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;
		String query = Constants.EmptyString;

		if (nameValuePairs != null && nameValuePairs.size() > 0) {
			query = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
		}
		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url
				+ (query == Constants.EmptyString ? Constants.EmptyString : Constants.QuestionMark + query));

		if (forceRefresh != null && forceRefresh.length > 0 && forceRefresh[0]) {
			restClient.setForceToRefresh(true);
		}
		restClient.SetIsJsonContent(true);
		SetRequestHeader(restClient);
		jsonObject = restClient.Execute(RestClient.RequestMethod.GET);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject GetWithoutToken(String url, List<NameValuePair> nameValuePairs, Boolean... forceRefresh)
			throws ServerAuthException, ServerGeneralException, Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;
		String query = Constants.EmptyString;

		if (nameValuePairs != null && nameValuePairs.size() > 0) {
			query = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
		}
		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url
				+ (query == Constants.EmptyString ? Constants.EmptyString : Constants.QuestionMark + query));

		if (forceRefresh != null && forceRefresh.length > 0 && forceRefresh[0]) {
			restClient.setForceToRefresh(true);
		}
		restClient.SetIsJsonContent(true);
		SetRequestHeaderWithoutToken(restClient);
		jsonObject = restClient.Execute(RestClient.RequestMethod.GET);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}



	private static JSONObject HandleResponseResult(JSONObject jsonObject) {
		return jsonObject;
	}

	private static void SetRequestHeaderWithoutToken(RestClient restClient) {
		if (restClient.isFilePost()) {
			// restClient.AddHeader(HTTP.CONTENT_TYPE, "binary/octet-stream");
			restClient.AddHeader(HTTP.CONTENT_TYPE, "text/plain; charset=utf-8");
		} else {
			restClient.AddHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
		}

		AddHeaderWithDeviceInfo(restClient);
	}

	private static void AddHeaderWithDeviceInfo(RestClient restClient) {
		DeviceManager deviceManager = DeviceManager.getInstance();
		restClient.AddHeader("DeviceModel", deviceManager.getDeviceModel());
		// restClient.AddHeader("SecondsFromGMT", deviceManager.getDeviceSecondsFromGMT());
		restClient.AddHeader("ConnectionType", deviceManager.getDeviceConnectionType());
		restClient.AddHeader("SDKVersion", deviceManager.getDeviceSDKVersion());
//		restClient.AddHeader("GeoLocation", deviceManager.getDeviceGeoLocationInfo());
		restClient.AddHeader("ClientVersion", deviceManager.getAppVersion());
		restClient.AddHeader(Constants.APP_ID,"34");
		restClient.AddHeader(Constants.DEVICE_ID,deviceManager.getDeviceId());

	}

	private static void SetRequestHeader(RestClient restClient) {
		SetRequestHeaderWithoutToken(restClient);
		restClient.AddHeader("X-YS-AccessToken", SessionManager.getInstance().getToken());
	}


	public static JSONObject Delete(String url, String postJsonString) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url);
		restClient.SetIsJsonContent(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", postJsonString);

		jsonObject = restClient.Execute(RestClient.RequestMethod.DELETE);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject Patch(String url, String postJsonString) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, url + " Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(SessionManager.getInstance().getAuthUser().getApi() + url);
		restClient.SetIsJsonContent(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", postJsonString);

		jsonObject = restClient.Execute(RestClient.RequestMethod.PATCH);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

}
