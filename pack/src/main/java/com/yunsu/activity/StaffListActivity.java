package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.view.TitleBar;

public class StaffListActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_staff)
    ListView lv_staff;

    @ViewById(id = R.id.tv_empty_staff_tip)
    private TextView tv_empty_staff_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.staff_list));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.create));
        lv_staff.setEmptyView(tv_empty_staff_tip);
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StaffListActivity.this,CreateStaffActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
//        lv_staff.setAdapter();


        super.onResume();
    }
}
