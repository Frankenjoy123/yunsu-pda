package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.activity.BaseActivity;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.manager.SettingManager;

import java.lang.reflect.Field;

public class GlobalSettingActivity extends BaseActivity {
    private TitleBar titleBar;

    @ViewById(id = R.id.tv_time_gap)
    private TextView tv_time_gap;

    @ViewById(id = R.id.rl_sync_data)
    private RelativeLayout rl_sync_data;

    @ViewById(id = R.id.rl_set_key_type)
    private RelativeLayout rl_set_key_type;

    @ViewById(id = R.id.tv_device_code)
    private TextView tv_device_code;

    @ViewById(id = R.id.tv_current_version)
    private TextView tv_current_version;

    @ViewById(id = R.id.rl_pack_setting)
    private RelativeLayout rl_pack_setting;

    @ViewById(id = R.id.rl_sync_time)
    private RelativeLayout rl_sync_time;

    public static final String PATTERN_ID="pattern_id";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_setting);
        init();
    }

    private void init() {
        getActionBar().hide();

        titleBar = (TitleBar) findViewById(R.id.global_setting_title_bar);
        titleBar.setTitle(getString(R.string.settings));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        tv_device_code.setText(DeviceManager.getInstance().getDeviceId());
        tv_current_version.setText(BuildConfig.VERSION_NAME);

        int syncMinute= SettingManager.getInstance().getSyncRateMin();
        tv_time_gap.setText("每隔"+ syncMinute+"分钟");

        bindClickEvent();
    }

    private void bindClickEvent() {
        rl_sync_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GlobalSettingActivity.this,SyncDataActivity.class);
                startActivity(intent);
            }
        });

        rl_set_key_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GlobalSettingActivity.this,KeyTypeSettingActivity.class);
                startActivity(intent);
            }
        });

        rl_pack_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(GlobalSettingActivity.this,PackSettingActivity.class);
                startActivity(intent);
            }
        });
        rl_sync_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(SettingManager.getInstance().getSyncRateMin());
            }
        });
    }

    private void dialog(int syncMinute){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.please_input_sync_rate);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_ll_tv,null);
        final EditText et= (EditText) view.findViewById(R.id.et_pack_key);
        et.setText(String.valueOf(syncMinute));
        et.setSelection(et.getText().length());
        builder.setView(view);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean closeDialog;
                String numString=et.getText().toString();
                if (numString.startsWith("0")){
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(GlobalSettingActivity.this,"请输入合法的数字",true);
                }else if (numString.length()<=3&&(Integer.parseInt(numString)<=120)){
                    closeDialog=true;
                    int min=Integer.parseInt(numString);
                    SettingManager.getInstance().saveSyncRateSetting(min);
                    tv_time_gap.setText("每隔"+ min+"分钟");
                }else {
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(GlobalSettingActivity.this,"请输入120分钟以内的数字",true);
                }
                try {
                    //下面三句控制弹框的关闭
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface,closeDialog);//true表示要关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //下面三句控制弹框的关闭
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface,true);//true表示要关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }



}
