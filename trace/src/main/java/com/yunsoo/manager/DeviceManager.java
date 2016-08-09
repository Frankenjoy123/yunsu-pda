package com.yunsoo.manager;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.yunsoo.network.NetworkManager;
import com.yunsoo.util.Constants;
import com.yunsoo.util.DensityUtil;
import com.yunsoo.util.HashUtils;
import com.yunsoo.util.HexStringUtils;
import com.yunsoo.util.StringHelper;


public class DeviceManager extends BaseManager {
	private static String TAG = DeviceManager.class.getSimpleName();
	private static DeviceManager deviceManager;

	private String deviceId;
	private String deviceModel;
	private String deviceSDKVersion;
	// private String secondsFromGMT;
	private String appVersion;

	private DeviceManager(Context context) {
		deviceModel = android.os.Build.MODEL;
		if (deviceModel == null) {
			deviceModel = Constants.NA;
		}

		deviceSDKVersion = "android: " + android.os.Build.VERSION.SDK_INT;

		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		if (deviceId == null) {
			String previousDeviceId = getDeviceIdFromPreference();
			if (StringHelper.isStringNullOrEmpty(previousDeviceId)) {
				deviceId = UUID.randomUUID().toString();
			} else {
				deviceId = previousDeviceId;
			}
		}
		byte[] data=HashUtils.sha1(deviceId+Constants.APP_ID_VALUE);
		deviceId=HexStringUtils.encode(data);
//		saveDeviceIdToPreference(deviceId);

		try {
			appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			appVersion = Constants.App.APP_VERSION;
		}

		// refreshTimeZone();
	}

	public static DeviceManager initializeIntance(Context context) {

		if (deviceManager == null) {
			deviceManager = new DeviceManager(context);
		}
		return deviceManager;
	}

	public static synchronized DeviceManager getInstance() {
		if (deviceManager == null) {
			Log.d(TAG, "deviceManager has not been initialized");
		}
		return deviceManager;
	}

	/*
	 * // refresh timezone public void refreshTimeZone() { int secondsFromGMTInt =
	 * Calendar.getInstance().getTimeZone().getRawOffset() / 1000; secondsFromGMT = Integer.toString(secondsFromGMTInt);
	 * }
	 */

	private void saveDeviceIdToPreference(String deviceId) {

		Editor editor = context.getSharedPreferences(Constants.Preference.PREF_DEVICE_INFO, Context.MODE_PRIVATE)
				.edit();
		editor.putString(Constants.Preference.KEY_DEVICE_INFO, deviceId);
		editor.commit();
	}

	private String getDeviceIdFromPreference() {
		SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_DEVICE_INFO,
				Context.MODE_PRIVATE);
		return preferences.getString(Constants.Preference.KEY_DEVICE_INFO, "");

	}

	public String getDeviceId() {
        Log.d("ZXW","deviceId"+deviceId);
		return deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public String getDeviceSDKVersion() {
		return deviceSDKVersion;
	}

	public String getDeviceConnectionType() {
		return NetworkManager.getInstance().getConnectionType();
	}

	public String getAppVersion() {
		return appVersion;
	}
}
