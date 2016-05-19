package com.yunsoo.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.view.TitleBar;

public class OffLineUploadActivity extends Activity {
    TitleBar titleBar;
    private TextView tv_off_line_step_2;
    private TextView tv_off_line_step_3;

    public static final String PACK_TYPE="pack_type";
    public static final String PATH_TYPE="path_type";

    public static final String OFFLINE_TYPE="offline_type";

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line_upload);
        getActionBar().hide();
        titleBar= (TitleBar) findViewById(R.id.off_line_upload_title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle("");

        tv_off_line_step_2= (TextView) findViewById(R.id.tv_off_line_step_2);
        tv_off_line_step_3= (TextView) findViewById(R.id.tv_off_line_step_3);

        type=getIntent().getStringExtra(OffLineUploadActivity.OFFLINE_TYPE);

        if (type!=null&&type.equals(OffLineUploadActivity.PATH_TYPE)){
            tv_off_line_step_2.setText(R.string.offline_path_step_2);
            tv_off_line_step_3.setText(R.string.offline_path_step_3);
        }

    }

}
