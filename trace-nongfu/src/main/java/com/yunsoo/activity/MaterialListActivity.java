package com.yunsoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yunsoo.adapter.OrderAdapter;
import com.yunsoo.annotation.ViewById;
import com.yunsoo.entity.MaterialEntity;
import com.yunsoo.view.TitleBar;

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

        final List<MaterialEntity> materialEntityList=new ArrayList<>();
        for(int i=1;i<=3;i++){
            MaterialEntity materialEntity=new MaterialEntity();
            materialEntity.setMaterialNumber("111101-0152"+i);
            materialEntity.setProductName("脐橙"+i+"星果"+"10公斤装");
            materialEntity.setAmount(3);
            materialEntityList.add(materialEntity);
        }

        orderAdapter=new OrderAdapter(this);
        orderAdapter.setMaterialEntityList(materialEntityList);
        lv_material.setAdapter(orderAdapter);

//        lv_material.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent=new Intent(MaterialListActivity.this,OutBoundScanActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putParcelable("Material",materialEntityList.get(i-1));
//                intent.putExtra("bundle",bundle);
//                startActivity(intent);
//            }
//        });
    }

}
