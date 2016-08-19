package com.yunsu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.yunsu.adapter.OrderAdapter;
import com.yunsu.annotation.ViewById;
import com.yunsu.entity.MaterialEntity;
import com.yunsu.service.ServiceExecutor;
import com.yunsu.sqlite.service.OrderService;
import com.yunsu.sqlite.service.impl.OrderServiceImpl;
import com.yunsu.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class MaterialListActivity extends BaseActivity {

    @ViewById(id = R.id.lv_material)
    private ListView lv_material;

    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list);
        init();
    }

    private void init() {
        getActionBar().hide();
        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(getString(R.string.material_list));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                OrderService orderService=new OrderServiceImpl();
                Order order=orderService.queryByOrderNumber("100");
                List<Material> materialList=order.getMaterials();
                Message message=Message.obtain();
                message.what=1;
                message.obj=materialList;
                handler.sendMessage(message);
            }
        });

//        final List<MaterialEntity> materialEntityList=new ArrayList<>();
//
//        for(int i=1;i<=3;i++){
//            MaterialEntity materialEntity=new MaterialEntity();
//            materialEntity.setMaterialNumber("111101-0152"+i);
//            materialEntity.setProductName("脐橙"+i+"星果"+"10公斤装");
//            materialEntity.setAmount(3);
//            materialEntityList.add(materialEntity);
//        }

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
             if (msg.what==1){
                 List<Material> materialList= (List<Material>) msg.obj;
                 orderAdapter=new OrderAdapter(MaterialListActivity.this);
                 orderAdapter.setMaterialList(materialList);
                 lv_material.setAdapter(orderAdapter);
             }

        }
    };

}
