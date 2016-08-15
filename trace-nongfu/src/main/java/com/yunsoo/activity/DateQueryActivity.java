package com.yunsoo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.adapter.ReportAdapter;
import com.yunsoo.entity.OrgAgency;
import com.yunsoo.manager.LogisticManager;
import com.yunsoo.service.ServiceExecutor;
import com.yunsoo.sqlite.SQLiteOperation;
import com.yunsoo.util.DateTimePickDialogUtil;
import com.yunsoo.view.TitleBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateQueryActivity extends Activity {

    private TitleBar titleBar;
    private DatePicker startDatePicker;
    private TextView tv_query_date;
    private RelativeLayout rl_query_date;
    private ListView listView;
    private TextView tv_empty_note;
    private TextView inTextView;
    private TextView outTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_query);
        init();
        initListView();
    }


    private void init(){
        getActionBar().hide();
        titleBar = (TitleBar) findViewById(R.id.data_report_title_bar);
        titleBar.setTitle(getString(R.string.data_report));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        inTextView= (TextView) findViewById(R.id.tv_report_inbound_count);
        outTextView= (TextView) findViewById(R.id.tv_report_outbound_count);
        listView= (ListView) findViewById(R.id.lv_yunsu_report);
        tv_query_date = (TextView) findViewById(R.id.tv_query_date);
        rl_query_date= (RelativeLayout) findViewById(R.id.rl_query_date);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        tv_query_date.setText(simpleDateFormat.format(new Date()));

        rl_query_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        DateQueryActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(tv_query_date,listView,inTextView,outTextView);
            }
        });
    }

    private void initListView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimePickDialogUtil.executeQueryReport(simpleDateFormat.format(new Date()),this,listView,inTextView,outTextView);
    }
}
