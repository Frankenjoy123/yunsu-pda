package com.yunsoo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.entity.AuthUser;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.view.TitleBar;

public class GlobalSettingActivity extends Activity {
    private TitleBar titleBar;
    private CheckBox cb_authorize_status;
    private boolean authorizeStatus;

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
        cb_authorize_status= (CheckBox) findViewById(R.id.cb_authorize_status);
    }

    private void setAuthorizeStatus() {
        SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
        authorizeStatus=preferences.getBoolean("isAuthorize", false);
        cb_authorize_status.setChecked(authorizeStatus);

        cb_authorize_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authorizeStatus = !authorizeStatus;
                cb_authorize_status.setChecked(authorizeStatus);
                if(!authorizeStatus){
                    AlertDialog dialog = new AlertDialog.Builder(GlobalSettingActivity.this).setTitle(R.string.cancel_authorize).setMessage(R.string.cancel_authorize_message)
                            .setPositiveButton(R.string.cancel_authorize, new DialogInterface.OnClickListener() {
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
                            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    authorizeStatus = !authorizeStatus;
                                    cb_authorize_status.setChecked(authorizeStatus);
                                }
                            }).create();
                    dialog.show();

                }
            }
        });

    }

}
