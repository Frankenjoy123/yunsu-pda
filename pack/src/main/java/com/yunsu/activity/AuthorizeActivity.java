package com.yunsu.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.entity.AuthorizeRequest;
import com.yunsu.common.entity.LoginResult;
import com.yunsu.common.entity.ScanAuthorizeInfo;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.AuthLoginService;
import com.yunsu.common.service.AuthorizeRegisterService;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.receiver.BarcodeReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthorizeActivity extends BaseActivity {

    private EditText et_authorize_code;
    private String content;
    private String api;
    private String accessToken;
    private String permanentToken;

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    private AuthorizeBarcodeReceiver mReceiver=new AuthorizeBarcodeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.app_name));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.TITLE_ONLY);
        et_authorize_code= (EditText) findViewById(R.id.et_authorize_code);
        bindScanAuthorize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BARCODE_SERVICE_BROADCAST);
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void bindScanAuthorize() {
        et_authorize_code.requestFocus();
        et_authorize_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                content=s.toString();
                doWithContent(content);
            }
        });
    }

    private  void doWithContent(String content){
        try {
            //start AuthLoginService
            JSONObject object=new JSONObject(content);
            String token=object.optString("t");
            api=object.optString("api");
            AuthUser tempAuthUser=new AuthUser();
            tempAuthUser.setApi(api);
            SessionManager.getInstance().saveLoginCredential(tempAuthUser);

            AuthLoginService authLoginService=new AuthLoginService(token);
            authLoginService.setDelegate(AuthorizeActivity.this);
            authLoginService.start();
            showLoading();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class AuthorizeBarcodeReceiver extends BarcodeReceiver{

        @Override
        protected void doWithReceiver(Intent intent) {
            content = intent.getExtras().getString(KEY_BARCODE_STR);
            doWithContent(content);
        }
    }

    @Override
    public void onRequestSucceeded(final DataServiceImpl service, final JSONObject data, boolean isCached) {
        Log.d("ZXW","register successfully");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(service instanceof AuthLoginService){
                    LoginResult loginResult=new LoginResult();
                    loginResult.populate(data);
                    accessToken=loginResult.getAccessToken();
                    permanentToken=loginResult.getPermanentToken();
                    AuthUser authUser=new AuthUser();
                    authUser.setApi(api);
                    authUser.setAccessToken(accessToken);
                    authUser.setPermanentToken(permanentToken);
                    SessionManager.getInstance().saveLoginCredential(authUser);
                    try {
                        JSONObject object=new JSONObject(content);

                        //set authorize request
                        ScanAuthorizeInfo scanAuthorizeInfo=new ScanAuthorizeInfo();
                        scanAuthorizeInfo.populate(object);
                        AuthorizeRequest request=new AuthorizeRequest();
                        request.setAccountId(scanAuthorizeInfo.getAccountId());
                        request.setComments(scanAuthorizeInfo.getDeviceComments());
                        DeviceManager deviceManager=DeviceManager.getInstance();
                        request.setDeviceCode(deviceManager.getDeviceId());
                        request.setDeviceName(scanAuthorizeInfo.getDeviceName());
                        request.setOs("Android");

                        //start device register service
                        AuthorizeRegisterService service=new AuthorizeRegisterService(request);
                        service.setDelegate(AuthorizeActivity.this);
                        service.start();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if(service instanceof AuthorizeRegisterService){
                    SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("isAuthorize",true);
                    editor.commit();

                    AuthUser authUser=SessionManager.getInstance().getAuthUser();
                    String orgId=data.optString("org_id");
                    AuthUser tempAuthUser=new AuthUser(authUser);
                    tempAuthUser.setOrgId(orgId);
                    SessionManager.getInstance().saveLoginCredential(tempAuthUser);

                    hideLoading();
                    ToastMessageHelper.showMessage(AuthorizeActivity.this,R.string.scan_success,true);

                    Intent intent=new Intent(AuthorizeActivity.this,PackMainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }

}
