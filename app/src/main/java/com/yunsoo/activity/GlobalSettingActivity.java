package com.yunsu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.yunsu.manager.SessionManager;
import com.yunsu.view.TitleBar;

public class GlobalSettingActivity extends Activity {
    private TitleBar titleBar;
    private Button btn_authorize_status;

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
        btn_authorize_status = (Button) findViewById(R.id.btn_authorize_status);
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

}
