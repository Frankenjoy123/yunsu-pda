package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.common.service.background.SyncLogService;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.service.background.RecycleHeartBeatService;
import com.yunsu.service.background.SyncFileService;

import org.json.JSONObject;

public class PackMainActivity extends BaseActivity implements View.OnClickListener {
    private AuthUser tempAuthUser;
    private String permanentToken;
    private String accessToken;
    private String api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_main);
        getActionBar().hide();
        setupActionItems();
        checkAuthorizeStatus();
        startService();
    }

    private void startService() {
        Intent intent=new Intent(this, SyncFileService.class);
        startService(intent);
        Intent intent1=new Intent(this, SyncLogService.class);
        intent1.putExtra(Constants.APP_TYPE,Constants.TRACE_APP);
        startService(intent1);
        Intent intent2=new Intent(this, RecycleHeartBeatService.class);
        startService(intent2);
    }

    private void checkAuthorizeStatus() {
        SessionManager.getInstance().restore();
        AuthUser authUser=SessionManager.getInstance().getAuthUser();
        permanentToken=authUser.getPermanentToken();
        accessToken=authUser.getAccessToken();
        api=authUser.getApi();
        PermanentTokenLoginService service= new PermanentTokenLoginService(permanentToken);
        service.setDelegate(PackMainActivity.this);
        service.start();
    }


    private void setupActionItems() {

        buildViewContent(this.findViewById(R.id.rl_pack_scan), R.drawable.ic_pack_scan, R.string.pack_scan);
        buildViewContent(this.findViewById(R.id.rl_pack_modify), R.drawable.ic_modify_pack_2, R.string.unpack_package);
        buildViewContent(this.findViewById(R.id.rl_data_report), R.drawable.ic_report, R.string.data_report);
        buildViewContent(this.findViewById(R.id.rl_settting), R.drawable.ic_setting_2, R.string.settings);
    }

    private void buildViewContent(View view, int imageResourceId, int textResourceId) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
        iv.setImageResource(imageResourceId);
        TextView tv = (TextView) view.findViewById(R.id.tv_action_name);
        tv.setText(textResourceId);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_pack_scan:
                Intent intent1=new Intent(PackMainActivity.this,PackSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_pack_modify:
                Intent intent2=new Intent(PackMainActivity.this,UnPackActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_data_report:
                Intent intent3=new Intent(PackMainActivity.this,ReportActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_settting:
                Intent intent4=new Intent(PackMainActivity.this,GlobalSettingActivity.class);
                startActivity(intent4);
                break;
        }
    }

    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof PermanentTokenLoginService){
            String newAccessToken=data.optString("token");
            int expires_in=data.optInt("expires_in");
            SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("isAuthorize", true);
            editor.commit();
            tempAuthUser=new AuthUser();
            tempAuthUser.setAccessToken(newAccessToken);
            tempAuthUser.setApi(api);
            tempAuthUser.setPermanentToken(permanentToken);
            SessionManager.getInstance().saveLoginCredential(tempAuthUser);
        }

    }

    @Override
    public void onRequestFailed(final DataServiceImpl service, final BaseException exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (service instanceof PermanentTokenLoginService && exception instanceof ServerAuthException){
                    SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("isAuthorize", false);
                    editor.commit();
                    SessionManager.getInstance().logout();
                    AlertDialog dialog = new AlertDialog.Builder(PackMainActivity.this).setTitle(R.string.not_authorize)
                            .setMessage(R.string.not_authorize_message)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(PackMainActivity.this,AuthorizeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }else {
                    ToastMessageHelper.showErrorMessage(PackMainActivity.this,exception.getMessage(),true);
                }
            }
        });

    }

}
