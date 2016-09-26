package com.yunsu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.ProductInPackageAdapter;
import com.yunsu.unity.PackageDetail;
import com.yunsu.common.view.TitleBar;

public class PackDetailActivity extends Activity {
	private TextView tv_packageID;
	private ListView lv_productInPack;
	private ProductInPackageAdapter adapter;
    private TitleBar titleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pack_detail);
        getActionBar().hide();

        titleBar=(TitleBar) findViewById(R.id.packDetail_title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle("包内清单");

		Intent intent=getIntent();
		PackageDetail detail=(PackageDetail) intent.getSerializableExtra("detail");
		tv_packageID=(TextView) findViewById(R.id.tv_packageID);
		lv_productInPack=(ListView) findViewById(R.id.lv_productInPack);
		tv_packageID.setText(detail.getPackageId());
		
		adapter=new ProductInPackageAdapter(PackDetailActivity.this, getResources());
		adapter.setProductIdList(detail.getProductIdList());
		
		lv_productInPack.setAdapter(adapter);
	}
}
