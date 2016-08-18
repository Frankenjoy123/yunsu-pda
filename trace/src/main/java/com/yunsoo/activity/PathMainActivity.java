package com.yunsoo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.exception.BaseException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsoo.manager.GreenDaoManager;
import com.yunsoo.manager.LogisticManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsoo.service.background.RecycleHeartBeatService;
import com.yunsoo.service.background.SyncFileService;
import com.yunsoo.service.background.SyncLogService;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class PathMainActivity extends BaseActivity implements View.OnClickListener {

    private LogisticActionAdapter actionAdapter;

    private String permanentToken;
    private String accessToken;
    private String api;
    private AuthUser tempAuthUser;
    TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_main);
        init();
        startService();
        getActionBar().hide();
        setupActionItems();
        checkAuthorizeStatus();
//        initData();
    }

    private void init() {
        getActionBar().hide();
        titleBar = (TitleBar) findViewById(R.id.path_main_title_bar);
        titleBar.setTitle(getString(R.string.home));
        titleBar.setMode(TitleBar.TitleBarMode.TITLE_ONLY);
    }

    /**
     * 启动同步文件日志的Service，启动心跳的Service
     */
    private void startService() {
        Intent intent=new Intent(this, SyncFileService.class);
        startService(intent);
        Intent intent1=new Intent(this, SyncLogService.class);
        startService(intent1);
        Intent intent2=new Intent(this, RecycleHeartBeatService.class);
        startService(intent2);
    }


    /**
     * 压力测试，用来初始化数据
     */
    private void initData() {
        if (Constants.INIT_DATA){

            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    SQLiteDatabase db=GreenDaoManager.getInstance().getDb();
                    String actionId=Constants.Logistic.INBOUND_CODE;
                    String agencyId=Constants.DEFAULT_STORAGE;
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String time="2016-05-07T15:00:00";
                    Log.d("TIME","start:"+dateFormat.format(new Date()));
                    db.beginTransaction();
                    SQLiteStatement statement=db.compileStatement("insert into pack values(null,?,?,?,?,?)");
                    for (int i=0;i<1000;i++){
                        String packKey= UUID.randomUUID().toString();
                        statement.bindString(1,packKey);
                        statement.bindString(2,actionId);
                        statement.bindString(3,agencyId);
                        statement.bindString(4,Constants.DB.SYNC);
                        statement.bindString(5,time);
                        statement.execute();
                        statement.clearBindings();
                    }
                    String actionId2=Constants.Logistic.OUTBOUND_CODE;
                    int size=  LogisticManager.getInstance().getAgencies().size();
                    Random random=new Random();
                    for (int j=0;j<10000;j++){
                        String agencyId2= LogisticManager.getInstance().getAgencies().get(random.nextInt(size)).getId();
                        String packKey= UUID.randomUUID().toString();
                        statement.bindString(1,packKey);
                        statement.bindString(2,actionId2);
                        statement.bindString(3,agencyId2);
                        statement.bindString(4,Constants.DB.SYNC);
                        statement.bindString(5,time);
                        statement.execute();
                        statement.clearBindings();
                    }

                    db.setTransactionSuccessful();
                    db.endTransaction();

                    Date date2=new Date();
                    String time2=dateFormat.format(date2);
                    Log.d("TIME","end"+time2);
                }
            });

        }
    }


    private void setupActionItems() {
        buildViewContent(this.findViewById(R.id.rl_action_inbound), R.drawable.ic_inbound, R.string.inbound_scan);
        buildViewContent(this.findViewById(R.id.rl_action_outbound), R.drawable.ic_outbound, R.string.outbound_scan);
        buildViewContent(this.findViewById(R.id.rl_action_revoke), R.drawable.ic_revoke, R.string.repeal_operation);
        buildViewContent(this.findViewById(R.id.rl_action_report), R.drawable.ic_report, R.string.data_report);
        buildViewContent(this.findViewById(R.id.rl_action_setting), R.drawable.ic_setting, R.string.settings);
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
            case R.id.rl_action_inbound:
                Intent inboundIntent=new Intent(PathMainActivity.this,PathActivity.class);
                inboundIntent.putExtra(Constants.Logistic.ACTION_ID,Constants.Logistic.INBOUND_CODE);
                inboundIntent.putExtra(Constants.Logistic.ACTION_NAME,Constants.Logistic.INBOUND);
                startActivity(inboundIntent);
                break;
            case R.id.rl_action_outbound:
                Intent outboundIntent=new Intent(PathMainActivity.this,OrgAgencyActivity.class);
                outboundIntent.putExtra(Constants.Logistic.ACTION_ID,Constants.Logistic.OUTBOUND_CODE);
                outboundIntent.putExtra(Constants.Logistic.ACTION_NAME,Constants.Logistic.OUTBOUND);
                startActivity(outboundIntent);
                break;
            case R.id.rl_action_revoke:
                Intent intent1=new Intent(PathMainActivity.this,RevokeOperationActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_action_report:
                Intent intent=new Intent(PathMainActivity.this,DateQueryActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_action_setting:
                Intent intent2=new Intent(PathMainActivity.this,GlobalSettingActivity.class);
                startActivity(intent2);
                break;
        }
    }

    /**
     * 检查授权状态
     */
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
        //授权验证成功后，更新状态
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
                //授权验证失败后，说明该设备被取消授权
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
