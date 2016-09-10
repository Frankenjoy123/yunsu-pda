package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.StaffAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.sqlite.service.StaffService;
import com.yunsu.sqlite.service.impl.StaffServiceImpl;

import java.util.List;

public class StaffListActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_staff)
    ListView lv_staff;

    @ViewById(id = R.id.tv_empty_staff_tip)
    private TextView tv_empty_staff_tip;

    private StaffAdapter staffAdapter;

    private StaffService staffService;

    private static final  int QUERY_ALL_STAFF_MSG=134;

    private List<Staff> staffList;

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
        staffAdapter=new StaffAdapter(this);
        staffService=new StaffServiceImpl();

        lv_staff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=getIntent();
                intent.putExtra(PackSettingActivity.STAFF_ID,staffList.get(i).getId());
                setResult(PackSettingActivity.STAFF_RESULT,intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {

        showLoading();
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                staffList=staffService.queryAllStaff();
                Message message=Message.obtain();
                message.what=QUERY_ALL_STAFF_MSG;
                message.obj=staffList;
                handler.sendMessage(message);

            }
        });

        super.onResume();
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_ALL_STAFF_MSG:
                    staffAdapter.setStaffList((List<Staff>) msg.obj);
                    lv_staff.setAdapter(staffAdapter);
                    hideLoading();
                    break;
            }


            super.handleMessage(msg);
        }
    };

}
