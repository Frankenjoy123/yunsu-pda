package com.yunsoo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.manager.SessionManager;
import com.yunsoo.manager.SettingManager;
import com.yunsoo.util.ToastMessageHelper;
import com.yunsoo.view.TitleBar;

import java.lang.reflect.Field;

public class GlobalSettingActivity extends Activity {
    private TitleBar titleBar;
    private Button btn_authorize_status;
    private TextView tv_time_gap;
    private RelativeLayout rl_auto_sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_setting);
        init();
        setAuthorizeStatus();
    }

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
