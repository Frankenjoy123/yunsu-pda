package com.yunsoo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.entity.AuthUser;
import com.yunsoo.exception.BaseException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.manager.LogisticManager;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.service.DataServiceImpl;
import com.yunsoo.service.PermanentTokenLoginService;
import com.yunsoo.service.background.SyncFileService;
import com.yunsoo.util.Constants;
import com.yunsoo.util.ToastMessageHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathMainActivity extends BaseActivity implements View.OnClickListener {

    ListView lv_action;
    private LogisticActionAdapter actionAdapter;

    List<Map<String,String>> actions;
    private String permanentToken;
    private String accessToken;
    private String api;
    private AuthUser tempAuthUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_main);
        startSyncFileService();
        initAction();
        getActionBar().hide();
        setupActionItems();
        checkAuthorizeStatus();
    }

    private void startSyncFileService() {
        Intent intent=new Intent(this, SyncFileService.class);
        startService(intent);
    }

    private void initAction() {

        try {
            lv_action= (ListView) findViewById(R.id.lv_action);
            actionAdapter=new LogisticActionAdapter(this);
            actions=new ArrayList<>();
            Map<String, String> map1=new HashMap();
            map1.put(Constants.Logistic.INBOUND_CODE, Constants.Logistic.INBOUND);
            Map<String, String> map2=new HashMap();
            map2.put(Constants.Logistic.OUTBOUND_CODE,Constants.Logistic.OUTBOUND);
            actions.add(map1);
            actions.add(map2);

            actionAdapter.setActions(actions);
            lv_action.setAdapter(actionAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupActionItems() {
//        buildViewContent(this.findViewById(R.id.rl_path_sync), R.drawable.ic_synchronize, R.string.sync_path);
        buildViewContent(this.findViewById(R.id.rl_data_report), R.drawable.ic_data_report, R.string.data_report);
        buildViewContent(this.findViewById(R.id.rl_path_setting), R.drawable.ic_my_settings, R.string.settings);
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
//            case R.id.rl_path_sync:
//                Intent intent1=new Intent(PathMainActivity.this,PathSyncActivity.class);
//                startActivity(intent1);
//                break;
            case R.id.rl_data_report:
                Intent intent=new Intent(PathMainActivity.this,DateQueryActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_path_setting:
                Intent intent2=new Intent(PathMainActivity.this,GlobalSettingActivity.class);
                startActivity(intent2);
                break;

        }
    }

    private void checkAuthorizeStatus() {
        SessionManager.getInstance().restore();
        AuthUser authUser=SessionManager.getInstance().getAuthUser();
        permanentToken=authUser.getPermanentToken();
        accessToken=authUser.getAccessToken();
        api=authUser.getApi();
        PermanentTokenLoginService service= new PermanentTokenLoginService(permanentToken);
        service.setDelegate(PathMainActivity.this);
        service.start();
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
            tempAuthUser.setOrgId(SessionManager.getInstance().getAuthUser().getOrgId());
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
                    AlertDialog dialog = new AlertDialog.Builder(PathMainActivity.this).setTitle(R.string.not_authorize)
                            .setMessage(R.string.not_authorize_message)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(PathMainActivity.this,AuthorizeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }else {
                    ToastMessageHelper.showErrorMessage(PathMainActivity.this,exception.getMessage(),true);
                }
            }
        });

    }
}
