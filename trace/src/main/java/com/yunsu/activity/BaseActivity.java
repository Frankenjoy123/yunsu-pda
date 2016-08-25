package com.yunsu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.dialog.LoadingDialog;
import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.entity.LoginResult;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;

import org.json.JSONObject;

import java.lang.reflect.Field;




public abstract class BaseActivity extends Activity implements DataServiceImpl.DataServiceDelegate {

    protected boolean isLoading;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        try {
            autowireViewForClass(this, this.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadingDialog = new LoadingDialog(this);
    }


    public void showLoading() {
        isLoading = true;
        loadingDialog.setCancelable(true);
        loadingDialog.show();
    }

    public void showLoading(DataServiceImpl service) {
        isLoading = true;
        loadingDialog.setCancelable(true);
        loadingDialog.setService(service);
        loadingDialog.show();
    }


    public void hideLoading() {
        isLoading = false;
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    private void autowireViewForClass(Activity activity, Class<? extends BaseActivity> clazz) throws Exception {

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ViewById.class)) {
                continue;
            }
            if (!View.class.isAssignableFrom(field.getType())) {
                continue;
            }
            ViewById viewId = field.getAnnotation(ViewById.class);
            int resId = viewId.id();


            if (resId == 0) {
                String resStringId = field
                        .getName();
                resId = activity.getResources().getIdentifier(resStringId, "id", activity.getPackageName());
            }
            View view = activity.findViewById(resId);
            if (view == null) {
                throw new RuntimeException("No view resource with the id of " + resId + " found. The required field " + field.getName() + " could not be autowired.");
            }
            field.setAccessible(true);
            field.set(activity, view);
        }
    }

    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {

    }

    @Override
    public void onRequestFailed(final DataServiceImpl service, final BaseException exception) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoading();
                if (service instanceof PermanentTokenLoginService && exception instanceof ServerAuthException){
                    SharedPreferences preferences=getSharedPreferences(Constants.Preference.YUNSU_PDA,MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean(Constants.Preference.IS_AUTHORIZE, false);
                    editor.commit();
                    SessionManager.getInstance().logout();
                    AlertDialog dialog = new AlertDialog.Builder(BaseActivity.this).setTitle(R.string.not_authorize)
                            .setMessage(R.string.not_authorize_message)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(BaseActivity.this,AuthorizeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }else {
                    ToastMessageHelper.showErrorMessage(BaseActivity.this,exception.getMessage(),true);
                }

            }
        });
    }


}
