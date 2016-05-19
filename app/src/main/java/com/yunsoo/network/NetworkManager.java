package com.yunsoo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yunsoo.activity.R;


public class NetworkManager {

	private static NetworkManager networkManager;
	private ConnectivityManager connectivityManager;
	private String connectionType;
	private Boolean isConnected;

	private String noNetworkErrorMessage;
	private NetworkManager() {

	}

	public static synchronized NetworkManager initializeIntance(Context appContext) {

		if (networkManager == null) {

			networkManager = new NetworkManager();
			networkManager.connectivityManager = (ConnectivityManager)appContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			networkManager.updateConnectStatus();


			String error_message = appContext.getString(R.string.network_not_available);
			networkManager.updateNoNetworkErrorMessage(error_message);

		}
		return networkManager;
	}

	private void updateNoNetworkErrorMessage(String error_message) {
		noNetworkErrorMessage = error_message;
	}

	public String getNoNetworkErrorMessage() {
		return noNetworkErrorMessage;
	}

	public static NetworkManager getInstance() {
		if (networkManager == null) {
			networkManager = new NetworkManager();
		}
		return networkManager;
	}

	public Boolean isNetworkConnected() {
		return isConnected;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void updateConnectStatus() {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		updateConnectStatus(networkInfo);
	}

	private void updateConnectStatus(NetworkInfo networkInfo) {
		if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
			isConnected = true;
			int netType = networkInfo.getType();

			switch (netType) {
			case ConnectivityManager.TYPE_WIFI:
				connectionType = "WIFI";
				break;
			case ConnectivityManager.TYPE_MOBILE:
				connectionType = "MOBILE";
				break;
			case ConnectivityManager.TYPE_MOBILE_DUN:
				connectionType = "MOBILE_DUN";
				break;
			case ConnectivityManager.TYPE_MOBILE_HIPRI:
				connectionType = "MOBILE_HIPRI";
				break;
			case ConnectivityManager.TYPE_MOBILE_MMS:
				connectionType = "MOBILE_MMS";
				break;
			case ConnectivityManager.TYPE_MOBILE_SUPL:
				connectionType = "MOBILE_SUPL";
				break;
			case ConnectivityManager.TYPE_WIMAX:
				connectionType = "WIMAX";
				break;

			default:
				connectionType = "N/A";
				break;
			}

		} else {
			isConnected = false;
			connectionType = "N/A";
		}

	}

}