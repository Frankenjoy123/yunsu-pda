package com.yunsu.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.yunsu.entity.AuthUser;

import com.yunsu.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;



/**
 * SessionManager is used for handling user related operations including: Login / logout, auth user info.
 * <p/>
 * FileName: SessionManager.java
 * <p/>
 * Description:
 * <p/>
 * Author: qyu (Qiyong Yu)
 * <p/>
 * Created Date: Nov 18, 2014 6:04:48 PM
 * <p/>
 * <p/>
 * Copyright (C) MicroStrategy Incorporated 2014 All Rights Reserved
 */

public class SessionManager extends BaseManager {

    private static SessionManager sessionManager;
    private static AuthUser authUser;
    private WeakReference<Activity> topActivityReference;

    public static SessionManager initializeIntance(Context context) {

        if (sessionManager == null) {
            synchronized (SessionManager.class) {
                if (sessionManager == null) {
                    sessionManager = new SessionManager();
                    sessionManager.context = context;
                }
            }
        }
        return sessionManager;
    }

    private SessionManager() {
        authUser = new AuthUser();
    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            Log.w("SessionManager", "SessionManager instance has not been initialized");
        }
        return sessionManager;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    // this should be in the base activity which can handle the login action.
    // when credential is required.
    public void pushActivity(Activity activity) {
        topActivityReference = new WeakReference<Activity>(activity);
    }

    public void restore() {

        SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_LOGIN,
                Context.MODE_PRIVATE);

        String authUserInfo = preferences.getString(Constants.Preference.KEY_USER_INFO, null);
        Log.d("ZXW", "authUserInfo: " + authUserInfo);
        if (authUserInfo != null) {
            try {
                JSONObject objectUser = new JSONObject(authUserInfo);
                if (authUser == null) {
                    authUser = new AuthUser();
                }
                authUser.populate(objectUser);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void saveLoginCredential(AuthUser user) {

        try {
            authUser = new AuthUser(user);
            Editor editor = context.getSharedPreferences(Constants.Preference.PREF_LOGIN, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.Preference.KEY_USER_INFO, authUser.toJsonString());
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return getAuthUser().getAccessToken();
    }


    private void clearLoginCredential() {
        Editor editor = context.getSharedPreferences(Constants.Preference.PREF_LOGIN, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        authUser = new AuthUser();
    }

    public void logout() {
        clearLoginCredential();
    }

    private void login(String phone, String pwd) {
        // Login service to call
        // LoginService service = new LoginService(phone, pwd);
        // service.setDelegate(this);
        // service.start();

    }

    public synchronized void reLogin() {
        /*
         * if (mReloginHandling) return;
		 * 
		 * if (mFacebook == null) { mFacebook = new Facebook(Constant.Emma.EMMA_APP_ID); } if (topActivityReference !=
		 * null && topActivityReference.get() != null) { mFacebook.authorize(topActivityReference.get(),
		 * Constant.Facebook.PERMISSION, mReLoginListener); mReloginHandling = true; }
		 */
    }

    /*
     * @Override public void onRequestSuccess(Object result, Object data) { if (data.equals(TASK_CALL_GRAPH)) {
     * mFBUserEmail = ((String[]) result)[0]; mFBUserId = ((String[]) result)[1]; mFBUserName = ((String[]) result)[2];
     * saveLoginCredential(); loginEmma(TASK_LOGIN_EMMA); } else if (data.equals(TASK_LOGIN_EMMA)) { //
     * Congratulations!! we complete all the login task, notify user // step into maintabs authUser = (AuthUser) result;
     * = mSessionUser.AuthToken; saveLoginCredential(); notifyLoginSuccess();
     *
     * EmmaActivityTaskData taskData = UserSettingsManager.getInstance().getTaskData(Task.TASK_GET_USER_SETTINGS,
     * Login.class); UserSettingsManager.getInstance().getUserSettings(taskData,true);
     *
     * } else if (data.equals(TASK_LOGOUT_EMMA)) { // logoutFacebook(); clearLoginCredential(); clearLoginInfo();
     * mIsLogout = true; logoutFacebook(); } else if (data.equals(TASK_SHARE_EMMA)) { mSessionUser = (AuthUser) result;
     * = mSessionUser.AuthToken; saveLoginCredential(); notifyGrantPermissionLogicSuccess(); } else if
     * (data.equals(TASK_RELOGIN_EMMA)) { mSessionUser = (AuthUser) result; = mSessionUser.AuthToken;
     * saveLoginCredential(); if (mTopActivityListener != null && mTopActivityListener.get() != null)
     * mTopActivityListener.get().onLoginComplete(true); } else if (data.equals(TASK_PREPARE_LOGIN)) { for (Object sl :
     * getListenersOfOneType(Session_Prepare_Login_Listener)) { ((SessionPauseListener) sl).onPauseComplete(); } } }
     *
     * @Override public void onRequestFailed(Object result, Object data) { if (data.equals(TASK_CALL_GRAPH)) {
     *
     * } else if (data.equals(TASK_LOGIN_EMMA)) { notifyLoginFail(); } else if (data.equals(TASK_LOGOUT_EMMA)) { //
     * logoutFacebook(); notifyLogoutCompleted(false); } else if (data.equals(TASK_RELOGIN_EMMA)) { if
     * (mTopActivityListener != null && mTopActivityListener.get() != null)
     * mTopActivityListener.get().onLoginComplete(false); } }
     */
//    public String getId() {
//        AuthUser user = getAuthUser();
////		if (!user.isAuthorized()) {
////			return DeviceManager.getInstance().getDeviceId();
////		} else {
////			return String.valueOf(user.getId());
////		}
//        return user.getUserId();
//    }


//    public void anonymousLogin(DataServiceImpl.DataServiceDelegate delegate) {
//
//        RegisterService service = new RegisterService(DeviceManager.getInstance().getDeviceId());
//        service.setDelegate(delegate);
//        service.start();
//    }


//    public void renewAccountCredential() {
//        AuthUser user = getAuthUser();
//        if (user.isAuthorized()) {
//            LoginService service = new LoginService(user.getPhone(), DeviceManager.getInstance().getDeviceId());
//            service.start();
//        }
////        else {
////            RegisterService service = new RegisterService(DeviceManager.getInstance().getDeviceId());
////            service.start();
////        }
//    }
}
