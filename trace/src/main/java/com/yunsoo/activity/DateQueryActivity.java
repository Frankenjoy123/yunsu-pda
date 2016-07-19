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
import com.yunsoo.util.DateTimePickDialogUtil;
import com.yunsoo.view.TitleBar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateQueryActivity extends Activity {

    private TitleBar titleBar;
    private DatePicker startDatePicker;
    private TextView tv_query_date;
    private RelativeLayout rl_query_date;
    private ListView listView;
    private TextView tv_empty_note;

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
        listView= (ListView) findViewById(R.id.lv_yunsu_report);
        tv_empty_note= (TextView) findViewById(R.id.tv_empty_note);
        listView.setEmptyView(tv_empty_note);
        tv_query_date = (TextView) findViewById(R.id.tv_query_date);
        rl_query_date= (RelativeLayout) findViewById(R.id.rl_query_date);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        tv_query_date.setText(simpleDateFormat.format(new Date()));

        rl_query_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        DateQueryActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(tv_query_date,listView);
            }
        });
    }

    private void initListView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimePickDialogUtil.executeQueryReport(simpleDateFormat.format(new Date()),this,listView);
    }
}
