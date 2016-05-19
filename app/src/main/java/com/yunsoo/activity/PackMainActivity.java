package com.yunsoo.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.fileOpreation.FileOperation;
import com.yunsoo.manager.LogisticManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PackMainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_main);
        getActionBar().hide();
        setupActionItems();
    }


    private void setupActionItems() {

        buildViewContent(this.findViewById(R.id.rl_pack_scan), R.drawable.ic_pack, R.string.pack_scan);
        buildViewContent(this.findViewById(R.id.rl_pack_modify), R.drawable.ic_modify_package, R.string.modify_package);
        buildViewContent(this.findViewById(R.id.rl_pack_upload), R.drawable.ic_synchronize, R.string.sync_pack);

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
                Intent intent1=new Intent(PackMainActivity.this,ScanActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_pack_modify:
                Intent intent2=new Intent(PackMainActivity.this,FixPackActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_pack_upload:

                Intent intent3=new Intent(PackMainActivity.this,PackSyncActivity.class);
                startActivity(intent3);
                break;
        }
    }

}
