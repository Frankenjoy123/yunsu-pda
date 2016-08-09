package com.yunsoo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.manager.FileManager;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.manager.SettingManager;
import com.yunsoo.network.CacheService;
import com.yunsoo.service.ServiceExecutor;
import com.yunsoo.util.ToastMessageHelper;
import com.yunsoo.view.TitleBar;

import java.lang.reflect.Field;

public class GlobalSettingActivity extends BaseActivity {
    private TitleBar titleBar;
    private Button btn_authorize_status;
    private TextView tv_time_gap;
    private RelativeLayout rl_auto_sync;
    private RelativeLayout rl_clear_cache;
    private TextView tv_cache_size;

    private final int CLEAR_SUCCESS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_setting);
        init();
        setAuthorizeStatus();
        setCacheSize();
    }

    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case   CLEAR_SUCCESS:
                    hideLoading();
                    setCacheSize();
            }
            super.handleMessage(msg);
        }
    };

    private void init() {
        getActionBar().hide();
        titleBar = (TitleBar) findViewById(R.id.global_setting_title_bar);
        titleBar.setTitle(getString(R.string.settings));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        rl_auto_sync= (RelativeLayout) findViewById(R.id.rl_auto_sync);
        btn_authorize_status = (Button) findViewById(R.id.btn_authorize_status);
        tv_time_gap= (TextView) findViewById(R.id.tv_time_gap);
        int syncMinute=SettingManager.getInstance().getSyncRateMin();
        tv_time_gap.setText("每隔"+ syncMinute+"分钟");
        rl_auto_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(SettingManager.getInstance().getSyncRateMin());
            }
        });
        rl_clear_cache= (RelativeLayout) findViewById(R.id.rl_clear_cache);
        tv_cache_size= (TextView) findViewById(R.id.tv_cache_size);
        rl_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(GlobalSettingActivity.this);
                builder.setTitle(R.string.clear_cache);
                builder.setMessage(R.string.clear_cache_release);
                builder.setNegativeButton(R.string.cancel,null);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoading();
                        clearCacheFile();
                    }
                });
                builder.show();
            }
        });

    }



    private void setCacheSize() {
        long size = FileManager.getInstance().getAllCacheSize();
        if (size < 1024 * 1024) {
            long kb = size / 1024;
            tv_cache_size.setText(String.valueOf(kb) + "KB");
        } else {
            long mb = size / (1024 * 1024);
            tv_cache_size.setText(String.valueOf(mb) + "MB");
        }
    }

    private void clearCacheFile() {
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                FileManager.getInstance().clearCache();
                Message message=Message.obtain();
                message.what=CLEAR_SUCCESS;
                handler.sendMessage(message);
            }
        });
    }


    private void setAuthorizeStatus() {

        btn_authorize_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(GlobalSettingActivity.this).setTitle(R.string.confirm_cancel_authorize).setMessage(R.string.cancel_authorize_message)
                        .setPositiveButton(R.string.confirm_cancel_authorize, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putBoolean("isAuthorize", false);
                                editor.commit();
                                SessionManager.getInstance().logout();
                                Intent intent=new Intent(GlobalSettingActivity.this,AuthorizeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton(R.string.no, null).create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });

    }

    private void dialog(int syncMinute){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.please_input_sync_rate);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_ll_tv,null);
        final EditText et= (EditText) view.findViewById(R.id.et_number);
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
