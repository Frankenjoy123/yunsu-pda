package com.yunsu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.entity.AuthorizeRequest;
import com.yunsu.common.entity.LoginResult;
import com.yunsu.entity.ScanAuthorizeInfo;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.AuthLoginService;
import com.yunsu.common.service.AuthorizeService;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.service.OrganizationAgencyService;
import com.yunsu.common.util.ToastMessageHelper;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthorizeActivity extends BaseActivity implements DataServiceImpl.DataServiceDelegate{

    private EditText et_authorize_code;
    private String content;
    private String api;
    private String accessToken;
    private String permanentToken;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        getActionBar().hide();
        et_authorize_code= (EditText) findViewById(R.id.et_authorize_code);
        bindScanAuthorize();


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
                try {
                    //start AuthLoginService
                    JSONObject object=new JSONObject(content);
                    String token=object.optString("t");
                    api=object.optString("api");
                    AuthUser tempAuthUser=new AuthUser();
                    tempAuthUser.setApi(api);
                    SessionManager.getInstance().saveLoginCredential(tempAuthUser);
                    SessionManager.getInstance().restore();

                    AuthLoginService authLoginService=new AuthLoginService(token);
                    authLoginService.setDelegate(AuthorizeActivity.this);
                    authLoginService.start();
                    showLoading();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Logger logger=Logger.getLogger(AuthorizeActivity.class);
                    logger.error(e.getMessage());
                }
            }
        });
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
                    AuthUser authUser=SessionManager.getInstance().getAuthUser();
                    AuthUser temp=new AuthUser();
                    temp.setApi(api);
                    temp.setAccessToken(accessToken);
                    temp.setPermanentToken(permanentToken);
                    SessionManager.getInstance().saveLoginCredential(temp);
                    SessionManager.getInstance().restore();
                    try {
                        JSONObject object=new JSONObject(content);

                        //set authorize request
                        ScanAuthorizeInfo scanAuthorizeInfo=new ScanAuthorizeInfo();
                        scanAuthorizeInfo.populate(object);
                        AuthorizeRequest request=new AuthorizeRequest();
                        request.setAccountId(scanAuthorizeInfo.getAccountId());
                        request.setComments(scanAuthorizeInfo.getDeviceComments());
                        DeviceManager.initializeIntance(AuthorizeActivity.this);
                        DeviceManager deviceManager=DeviceManager.getInstance();
                        request.setDeviceCode(deviceManager.getDeviceId());
                        request.setDeviceName(scanAuthorizeInfo.getDeviceName());
                        request.setOs("Android");

                        //start device register service
                        AuthorizeService service=new AuthorizeService(request);
                        service.setDelegate(AuthorizeActivity.this);
                        service.start();

                        OrganizationAgencyService organizationAgencyService=new OrganizationAgencyService();
                        organizationAgencyService.start();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if(service instanceof AuthorizeService){
                    SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                    String orgId=data.optString("org_id");
                    SessionManager manager=SessionManager.getInstance();
                    AuthUser authUser=manager.getAuthUser();
                    AuthUser temp=new AuthUser();
                    temp.setOrgId(orgId);
                    temp.setAccessToken(authUser.getAccessToken());
                    temp.setPermanentToken(authUser.getPermanentToken());
                    temp.setApi(authUser.getApi());
                    manager.saveLoginCredential(temp);
                    SessionManager.getInstance().restore();
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("isAuthorize",true);
                    editor.commit();
                    hideLoading();
                    ToastMessageHelper.showMessage(AuthorizeActivity.this,R.string.scan_success,true);

                    Intent intent=new Intent(AuthorizeActivity.this,PathMainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }

    @Override
    public void onRequestFailed(DataServiceImpl service, BaseException exception) {
        Log.d("ZXW","register failed");
        ToastMessageHelper.showErrorMessage(AuthorizeActivity.this,R.string.authorize_failed,true);
        super.onRequestFailed(service,exception);
    }
}
