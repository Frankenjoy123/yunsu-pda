package com.yunsu.common.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.android_library.R;
import com.yunsu.common.util.ToastMessageHelper;


public class NetworkChangeReceiver extends BroadcastReceiver {

    protected Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        showNetStatus();
    }


    private void showNetStatus() {
        NetworkManager.getInstance().updateConnectStatus();
        boolean isNetWork=NetworkManager.getInstance().isNetworkConnected();
        if (isNetWork){
            ToastMessageHelper.showMessage(mContext,R.string.net_connect,true);
        }else {
            ToastMessageHelper.showMessage(mContext, R.string.net_disconnect,true);
        }
    }
}