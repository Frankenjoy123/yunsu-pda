package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.view.TitleBar;

public class PackDefineActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.rl_choose_standard)
    private RelativeLayout rl_choose_standard;

    @ViewById(id = R.id.rl_choose_staff)
    private RelativeLayout rl_choose_staff;

    @ViewById(id = R.id.rl_choose_product)
    private RelativeLayout rl_choose_product;

    public static final int STAFF_REQUEST=123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pack);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.pack_setting));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        rl_choose_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent staffIntent = new Intent(PackDefineActivity.this, StaffListActivity.class);
                startActivity(staffIntent);
            }
        });

    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.rl_choose_staff:
//                Intent staffIntent = new Intent(this, StaffListActivity.class);
//                startActivity(staffIntent);
//                break;
//
//        }
//    }
}
