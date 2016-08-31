package com.yunsu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.yunsu.adapter.PackageHistoryAdapter;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.ProductService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.sqlite.service.impl.ProductServiceImpl;
import com.yunsu.unity.PackageDetail;

import java.util.ArrayList;
import java.util.List;

public class PackedHistoryActivity extends Activity {
	
	private ListView lv;
	private List<PackageDetail> detailList=new ArrayList<>();
	private PackageHistoryAdapter adapter;
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_packed_history);
		getActionBar().hide();

        init();
        queryFromDB();
	}

    private void queryFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PackService packService=new PackServiceImpl();
                ProductService productService=new ProductServiceImpl();
                List<Pack> packList=packService.queryAllPack();
                for (int i=0;i<packList.size();i++){
                    Pack pack=packList.get(i);
                    PackageDetail packageDetail=new PackageDetail();
                    packageDetail.setPackageId(packList.get(i).getPackKey());
                    List<Product> productList=productService.queryAllProductByPackId(pack.getId());
                    List<String> productKeyList=new ArrayList<String>();
                    for(int j=0;j<productList.size();j++){
                        productKeyList.add(productList.get(j).getProductKey());
                    }
                    packageDetail.setProductIdList(productKeyList);
                    detailList.add(packageDetail);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setPackageDetailList(detailList);
//                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void init() {
        titleBar = (TitleBar) findViewById(R.id.packHistory_title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle("打包历史");

        lv = (ListView) findViewById(R.id.lv_hasPacked);
        adapter = new PackageHistoryAdapter(this, getResources());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(PackedHistoryActivity.this,
                        PackDetail.class);
                PackageDetail detail = detailList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("detail", detail);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
