package com.yunsu.common.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.yunsu.common.util.StringHelper;

import org.apache.log4j.Logger;


public class FileLocationManager extends BaseManager {
	private static String TAG = FileLocationManager.class.getSimpleName();
	private static FileLocationManager fileLocationManager;

	private String commonLogTaskFolder;

	private String commonLogSuccessFolder;

	private String crashLogTaskFolder;

	private String crashLogSuccessFolder;

	private String dataTaskFolder;

	private String org;

	private String business;

	private FileLocationManager(Context context) {

		ApplicationInfo appInfo = null;


		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			org=appInfo.metaData.getString("org_folder");
			business= appInfo.metaData.getString("business_folder");
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}


		dataTaskFolder=getBusinessFolder().append("/").append("数据").toString();

		commonLogTaskFolder=getBusinessFolder().append("/").append("log").append("/").append("not_sync").toString();

		commonLogSuccessFolder=getBusinessFolder().append("/").append("log").append("/").append("sync_success").toString();

		crashLogTaskFolder=getBusinessFolder().append("/").append("log").append("/").append("crash").append("/").append("not_sync").toString();

		crashLogSuccessFolder=getBusinessFolder().append("/").append("log").append("/").append("crash").append("/").append("sync").toString();
	}



	private StringBuilder getBusinessFolder(){
		StringBuilder builder= new StringBuilder();
		builder.append(android.os.Environment.getExternalStorageDirectory().toString());
		if (!StringHelper.isStringNullOrEmpty(org));
		{
			builder.append("/");
			builder.append(org);
		}

		if (!StringHelper.isStringNullOrEmpty(business)){
			builder.append("/");
			builder.append(business);
		}

		return builder;
	}


	public static FileLocationManager initializeIntance(Context context) {

		if (fileLocationManager == null) {
			fileLocationManager = new FileLocationManager(context);
		}
		return fileLocationManager;
	}

	public static synchronized FileLocationManager getInstance() {
		if (fileLocationManager == null) {
			Log.d(TAG, "fileLocationManager has not been initialized");
			Logger logger=Logger.getLogger(FileLocationManager.class);
			logger.error("fileLocationManager has not been initialized");
		}
		return fileLocationManager;
	}

	public String getCommonLogTaskFolder() {
		return commonLogTaskFolder;
	}

	public String getCommonLogSuccessFolder() {
		return commonLogSuccessFolder;
	}

	public String getCrashLogTaskFolder() {
		return crashLogTaskFolder;
	}

	public String getCrashLogSuccessFolder() {
		return crashLogSuccessFolder;
	}

	public String getDataTaskFolder() {
		return dataTaskFolder;
	}
}
