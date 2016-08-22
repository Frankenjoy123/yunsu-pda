package com.yunsu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.view.TitleBar;

import java.util.List;

public class OrderListActivity extends  BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;
    @ViewById(id = R.id.lv_order)
    ListView lv_order;

    public static final int ADD_NEW_ORDER_REQUEST=123;

    public static final int ADD_NEW_ORDER_RESULT=456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.outbound_order));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.add));
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderListActivity.this,CreateOrderActivity.class);
                startActivityForResult(intent,ADD_NEW_ORDER_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==ADD_NEW_ORDER_RESULT&&requestCode==ADD_NEW_ORDER_REQUEST){

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

