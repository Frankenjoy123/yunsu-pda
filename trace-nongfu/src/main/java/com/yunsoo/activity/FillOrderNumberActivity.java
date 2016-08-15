package com.yunsoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yunsoo.annotation.ViewById;
import com.yunsoo.view.TitleBar;

public class FillOrderNumberActivity extends BaseActivity {

    @ViewById(id = R.id.btn_query_order)
    private Button btn_query_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_order_number);

        init();

        btn_query_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FillOrderNumberActivity.this,MaterialListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        getActionBar().hide();
        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(getString(R.string.fill_order_number));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

    }
}
