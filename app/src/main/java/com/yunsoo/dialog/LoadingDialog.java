package com.yunsoo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.yunsoo.activity.R;
import com.yunsoo.service.DataServiceImpl;


/**
 * Created by Dake Wang on 2015/6/2.
 */
public class LoadingDialog {


    private Dialog dialog;
    private Context context;
    private String loadingText;

    public LoadingDialog(Context context) {
        this.context = context;
        initDialog();
    }

    private void initDialog() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_loading, null);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Fixed the white corner issue.
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(rootView);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (service != null) {
                    service.cancel();
                }
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (service != null) {
            service.cancel();
            service = null;
        }
        dialog.dismiss();
    }

    DataServiceImpl service;

    public void setService(DataServiceImpl service) {
        this.service = service;
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void setCancelable(boolean cancelable) {
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
    }
}
