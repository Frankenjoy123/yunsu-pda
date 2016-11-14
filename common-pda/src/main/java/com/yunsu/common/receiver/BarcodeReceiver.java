package com.yunsu.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

/**
 * Created by yunsu on 2016/11/11.
 */
public abstract class BarcodeReceiver extends BroadcastReceiver {

    private Date lastScanDate;

    //send by BarcodeService
    public static final String ACTION_BARCODE_SERVICE_BROADCAST = "action_barcode_broadcast";
    //send by the BarcodeService
    public static final String KEY_BARCODE_STR = "key_barcode_string";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BARCODE_SERVICE_BROADCAST)) {
            if (lastScanDate==null){
                lastScanDate=new Date();
                doWithReceiver(intent);
            }else {
                Date date=new Date();
                // 防止扫码抖动
                if ((date.getTime()-lastScanDate.getTime())>300){
                    lastScanDate=date;
                    doWithReceiver(intent);
                }
            }
        }
    }

    protected abstract void doWithReceiver(Intent intent);
}
