package com.yunsoo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.yunsoo.annotation.ViewById;
import com.yunsoo.dialog.LoadingDialog;
import com.yunsoo.entity.AuthUser;
import com.yunsoo.entity.LoginResult;
import com.yunsoo.exception.BaseException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.service.DataServiceImpl;
import com.yunsoo.service.PermanentTokenLoginService;
import com.yunsoo.util.ToastMessageHelper;

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
    public void onRequestSucceeded(final DataServiceImpl service, final JSONObject data, boolean isCached) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoading();
                if (service instanceof PermanentTokenLoginService){
                    AuthUser authUser=SessionManager.getInstance().getAuthUser();
                    AuthUser tempAuthUser=new AuthUser();
                    tempAuthUser.setApi(authUser.getApi());
                    tempAuthUser.setPermanentToken(authUser.getPermanentToken());
                    LoginResult loginResult=new LoginResult();
                    loginResult.populate(data);
                    tempAuthUser.setAccessToken(loginResult.getAccessToken());
                    SessionManager.getInstance().saveLoginCredential(tempAuthUser);
                    SessionManager.getInstance().restore();
                }
            }
        });
    }

    @Override
    public void onRequestFailed(final DataServiceImpl service, final BaseException exception) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoading();
                if (service instanceof PermanentTokenLoginService && exception instanceof ServerAuthException){
                    SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("isAuthorize", false);
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
