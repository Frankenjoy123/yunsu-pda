package com.yunsu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

    public static final int ADD_NEW_ORDER_REQUEST=123;

    public static final int ADD_NEW_ORDER_RESULT=456;

    List<Material> materialList=new ArrayList<>();

    private MaterialService materialService;

    private OrderAdapter orderAdapter;

    public static final String ID="id";

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
        titleBar.setRightButtonText(getString(R.string.add));
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
                intent.putExtra(ID,materialList.get(i).getId());
                startActivity(intent);
//                ToastMessageHelper.showMessage(OrderListActivity.this,"jjj",true);
            }
        });
    }


    @Override
    protected void onResume() {
        List<Material> materialList=materialService.queryAllMaterial();
        this.materialList.clear();
        this.materialList.addAll(materialList);
        orderAdapter.notifyDataSetChanged();
        super.onResume();
    }

}
