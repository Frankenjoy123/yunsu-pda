package com.yunsoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.yunsoo.adapter.PackageHistoryAdapter;
import com.yunsoo.fileOpreation.PackDetailFileRead;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.sqlite.SQLiteOperation;
import com.yunsoo.unity.PackageDetail;
import com.yunsoo.util.Constants;
import com.yunsoo.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

public class PackedHistoryActivity extends Activity {
	
	private ListView lv;
	private List<PackageDetail> detailList=new ArrayList<>();
	private PackageHistoryAdapter adapter;
	private TitleBar titleBar;

    MyDataBaseHelper dataBaseHelper;

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
                dataBaseHelper=new MyDataBaseHelper(PackedHistoryActivity.this, Constants.SQ_DATABASE,null,1);
                Cursor cursor=dataBaseHelper.getReadableDatabase().rawQuery("select * from pack order by _id desc limit 100", null);

                while (cursor.moveToNext()){
                    String pack_key=cursor.getString(1);
                    String product_keys=cursor.getString(2);
                    PackageDetail packageDetail=new PackageDetail();
                    packageDetail.setPackageId(pack_key);
                    String[] arrayStrings=product_keys.split(",");
                    List<String> products=new ArrayList<String>();

                    for(int j=0;j<arrayStrings.length;j++){
                        products.add(arrayStrings[j]);
                    }
                    packageDetail.setProductIdList(products);
                    detailList.add(packageDetail);
                }
                dataBaseHelper.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
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
        adapter.setPackageDetailList(detailList);
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
