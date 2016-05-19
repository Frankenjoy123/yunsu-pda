package com.yunsoo.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.manager.LogisticManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathMainActivity extends BaseActivity implements View.OnClickListener {

    ListView lv_action;
    private LogisticActionAdapter actionAdapter;

    List<Map<Integer,String>> actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_main);
        initAction();
        getActionBar().hide();
        setupActionItems();
    }

    private void initAction() {

        try {
            lv_action= (ListView) findViewById(R.id.lv_action);
            actionAdapter=new LogisticActionAdapter(this);
            actions=LogisticManager.getInstance().getActionList();
//            if (actions==null||actions.size()<1){
//                actions=new ArrayList<>();
//                Map<Integer, String> map1=new HashMap();
//                map1.put(100,"入库");
//                Map<Integer, String> map2=new HashMap();
//                map2.put(200,"出库");
//                actions.add(map1);
//                actions.add(map2);
//            }
            actionAdapter.setActions(actions);
            lv_action.setAdapter(actionAdapter);

            lv_action.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupActionItems() {
        buildViewContent(this.findViewById(R.id.rl_path_sync), R.drawable.ic_synchronize, R.string.sync_path);

    }

    private void buildViewContent(View view, int imageResourceId, int textResourceId) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
        iv.setImageResource(imageResourceId);
        TextView tv = (TextView) view.findViewById(R.id.tv_action_name);
        tv.setText(textResourceId);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_path_sync:
                Intent intent1=new Intent(PathMainActivity.this,PathSyncActivity.class);
                startActivity(intent1);
                break;

        }
    }
}
