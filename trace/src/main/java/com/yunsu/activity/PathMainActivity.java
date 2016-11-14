package com.yunsu.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.activity.BaseActivity;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.entity.AuthUser;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.DataServiceImpl;
import com.yunsu.common.service.PermanentTokenLoginService;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.service.background.SyncLogService;
import com.yunsu.common.util.Constants;
import com.yunsu.common.view.TitleBar;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.service.OrganizationAgencyService;
import com.yunsu.service.background.RecycleHeartBeatService;
import com.yunsu.service.background.SyncFileService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class PathMainActivity extends BaseActivity implements View.OnClickListener {

    TitleBar titleBar;

    public static PathMainActivity pathMainActivity;


    @ViewById(id = R.id.rl_action_inbound)
    RelativeLayout rl_action_inbound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_main);
        init();
        setupActionItems();
        checkAuthorizeStatus();
        OrganizationAgencyService organizationAgencyService=new OrganizationAgencyService();
        organizationAgencyService.start();
//        initData();
    }

    /**
     * 检查授权状态
     */
    private void checkAuthorizeStatus() {
        AuthUser authUser=SessionManager.getInstance().getAuthUser();
        String permanentToken = authUser.getPermanentToken();
        PermanentTokenLoginService service= new PermanentTokenLoginService(permanentToken);
        service.setContext(this);
        service.setDelegate(this);
        service.start();
    }

    /**
     * 启动同步文件日志的Service，启动心跳的Service
     */
    private void startService() {

        if (LogisticManager.getInstance().getAgencies()!=null && LogisticManager.getInstance().getAgencies().size()>0){
            OrganizationAgencyService organizationAgencyService=new OrganizationAgencyService();
            organizationAgencyService.start();
        }

        Intent intent=new Intent(this, SyncFileService.class);
        startService(intent);
        Intent intent1=new Intent(this, SyncLogService.class);
        intent1.putExtra(Constants.APP_TYPE,Constants.TRACE_APP);
        startService(intent1);
        Intent intent2=new Intent(this, RecycleHeartBeatService.class);
        startService(intent2);
    }


    private void init() {
        getActionBar().hide();
        titleBar = (TitleBar) findViewById(R.id.path_main_title_bar);
        titleBar.setTitle(getString(R.string.home));
        titleBar.setMode(TitleBar.TitleBarMode.TITLE_ONLY);
        pathMainActivity=this;
        rl_action_inbound.setEnabled(false);
    }


    private void setupActionItems() {
        buildViewContent(this.findViewById(R.id.rl_action_inbound), R.drawable.ic_inbound, R.string.inbound_scan);
        buildViewContent(this.findViewById(R.id.rl_action_outbound), R.drawable.ic_outbound, R.string.outbound_order);
        buildViewContent(this.findViewById(R.id.rl_action_report), R.drawable.ic_report, R.string.data_report);
        buildViewContent(this.findViewById(R.id.rl_action_setting), R.drawable.ic_setting, R.string.settings);
    }

    private void buildViewContent(View view, int imageResourceId, int textResourceId) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
        iv.setImageResource(imageResourceId);
        TextView tv = (TextView) view.findViewById(R.id.tv_action_name);
        tv.setText(textResourceId);
        if (textResourceId==R.string.inbound_scan){
            tv.setTextColor(getResources().getColor(R.color.grey));
        }
        view.setOnClickListener(this);
    }

    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof PermanentTokenLoginService){
            startService();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_action_inbound:
                Intent inboundIntent=new Intent(PathMainActivity.this,InboundScanActivity.class);
                startActivity(inboundIntent);
                break;
            case R.id.rl_action_outbound:
                Intent outboundIntent=new Intent(PathMainActivity.this,OrderListActivity.class);
                outboundIntent.putExtra(Constants.Logistic.ACTION_ID,Constants.Logistic.OUTBOUND_CODE);
                outboundIntent.putExtra(Constants.Logistic.ACTION_NAME,Constants.Logistic.OUTBOUND);
                startActivity(outboundIntent);
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

}
