package com.yunsu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.sqlite.service.StaffService;
import com.yunsu.sqlite.service.impl.StaffServiceImpl;

public class CreateStaffActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_staff_name)
    private EditText et_staff_name;

    @ViewById(id = R.id.et_staff_number)
    private EditText et_staff_number;

    @ViewById(id = R.id.btn_create_staff)
    private Button btn_create_staff;

    @ViewById(id = R.id.rl_root_create_staff)
    private RelativeLayout rl_root_create_staff;

    private StaffService staffService=new StaffServiceImpl();

    private static final int INSERT_NEW_MSG=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_staff);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.create_staff));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);

        btn_create_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String staffName=et_staff_name.getText().toString();
                final String staffNumber=et_staff_number.getText().toString();
                if (StringHelper.isStringNullOrEmpty(staffName)){
                    ToastMessageHelper.showErrorMessage(CreateStaffActivity.this,R.string.name_not_null,true);
                }else if (StringHelper.isStringNullOrEmpty(staffNumber)){
                    ToastMessageHelper.showErrorMessage(CreateStaffActivity.this,R.string.number_not_null,true);
                }else {
                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            Staff staff=new Staff(null,staffNumber,staffName);
                            long id=staffService.insert(staff);
                            Message message=new Message();
                            message.what=INSERT_NEW_MSG;
                            message.obj=id;
                            handler.sendMessage(message);
                        }
                    });



                }
            }
        });

        rl_root_create_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==INSERT_NEW_MSG){
                Intent intent=getIntent();
                intent.putExtra(Constants.STAFF_ID,(long)msg.obj);
                setResult(StaffListActivity.CREATE_NEW_STAFF_RESULT,intent);
                finish();
            }
        }
    };

}
