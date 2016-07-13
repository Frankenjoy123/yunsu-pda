package com.yunsoo.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ListView;

import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.adapter.PathAdapter;
import com.yunsoo.fileOpreation.FileRead;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.unity.PackageDetail;
import com.yunsoo.util.Constants;
import com.yunsoo.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

public class PathHistoryActivity extends Activity {
	private ListView lv_pathHistory;
	private PathAdapter historyAdapter;
	private List<String> historyList=new ArrayList<>();
	private FileRead historyFileReader;
    private TitleBar titleBar;

    private MyDataBaseHelper dataBaseHelper;
    private String actionName;
    private int actionId;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_history);
        actionName=getIntent().getStringExtra(LogisticActionAdapter.ACTION_NAME);
        actionId=getIntent().getIntExtra(LogisticActionAdapter.ACTION_ID,0);

        dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
        getActionBar().hide();
        titleBar=(TitleBar) findViewById(R.id.pathHistory_title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(actionName+"扫描历史");

        lv_pathHistory=(ListView) findViewById(R.id.lv_pathHistory);
        historyAdapter=new PathAdapter(this, getResources());
        historyAdapter.setKeyList(historyList);
        lv_pathHistory.setAdapter(historyAdapter);


        getFromDB();
    }

    private void getFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Cursor cursor=dataBaseHelper.getReadableDatabase()
                            .rawQuery("select * from path where action_id=? order by _id desc limit 100",
                            new String[]{String.valueOf(actionId)});
                    while (cursor.moveToNext()){
                        String pack_key=cursor.getString(1);
                        historyList.add(pack_key);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            historyAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
