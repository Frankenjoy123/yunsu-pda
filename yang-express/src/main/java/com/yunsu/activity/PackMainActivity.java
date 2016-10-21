package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.common.service.background.SyncLogService;
import com.yunsu.common.util.Constants;
import com.yunsu.service.background.RecycleHeartBeatService;
import com.yunsu.service.background.SyncFileService;

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
                Intent intent1=new Intent(PackMainActivity.this,PackScanActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_settting:
                Intent intent4=new Intent(PackMainActivity.this,GlobalSettingActivity.class);
                startActivity(intent4);
                break;
        }
    }

}
