package com.yunsu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.OrderAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends  BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_order)
    ListView lv_order;

    @ViewById(id = R.id.tv_empty_order_tip)
    private TextView tv_empty_order_tip;

    public static final int ADD_NEW_ORDER_REQUEST=123;

    public static final int ADD_NEW_ORDER_RESULT=456;

    public static final int QUERY_ORDER_LIST_MSG=167;

    List<Material> materialList=new ArrayList<>();

    private MaterialService materialService;

    private OrderAdapter orderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.outbound_order));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.new_order));
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderListActivity.this,CreateOrderActivity.class);
                startActivityForResult(intent,ADD_NEW_ORDER_REQUEST);
            }
        });
        materialService=new MaterialServiceImpl();
        orderAdapter=new OrderAdapter(this);
        orderAdapter.setMaterialList(materialList);
        lv_order.setAdapter(orderAdapter);
        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(OrderListActivity.this,OrderScanActivity.class);
                intent.putExtra(Constants.DB.ID,materialList.get(i).getId());
                startActivity(intent);
            }
        });

        lv_order.setEmptyView(tv_empty_order_tip);
    }


    @Override
    protected void onResume() {
        showLoading();
        ServiceExecutor.getInstance().execute(() -> {
            List<Material> materialList=materialService.queryAllMaterial();
            this.materialList.clear();
            this.materialList.addAll(materialList);
            handler.sendEmptyMessage(QUERY_ORDER_LIST_MSG);
        });
        super.onResume();
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case QUERY_ORDER_LIST_MSG:
                    hideLoading();
                    orderAdapter.notifyDataSetChanged();
                    break;

            }
            super.handleMessage(msg);
        }


    };

}

