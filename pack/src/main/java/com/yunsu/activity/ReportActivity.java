package com.yunsu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.view.TitleBar;
import com.yunsu.util.DateTimePickDialogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    private DatePicker startDatePicker;

    @ViewById(id = R.id.tv_query_date)
    private TextView tv_query_date;

    @ViewById(id = R.id.rl_query_date)
    private RelativeLayout rl_query_date;

    @ViewById(id = R.id.lv_yunsu_report)
    private ListView listView;

    @ViewById(id = R.id.tv_pack_total_count)
    private TextView tv_pack_total_count;

    @ViewById(id = R.id.tv_product_total_count)
    private TextView tv_product_total_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        init();
        initListView();
    }


    private void init(){
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.data_report));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        tv_query_date.setText(simpleDateFormat.format(new Date()));

        rl_query_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        ReportActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(tv_query_date,listView,tv_pack_total_count,tv_product_total_count);
            }
        });
    }

    private void initListView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimePickDialogUtil.executeQueryReport(simpleDateFormat.format(new Date()),this,listView,tv_pack_total_count,tv_product_total_count);
    }
}
