package com.yunsoo.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.adapter.OrgAgencyAdapter;
import com.yunsoo.manager.LogisticManager;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.util.Constants;
import com.yunsoo.view.TitleBar;

import java.util.List;

public class OrgAgencyActivity extends Activity {
    private TitleBar titleBar;
    private ListView listView;
    private OrgAgencyAdapter adapter;
    private List<String> orgAgencyList;

    private int actionId;
    private String actionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_agency);
        init();
    }

    private void init(){
        getActionBar().hide();
        actionId=getIntent().getIntExtra(LogisticActionAdapter.ACTION_ID,0);
        actionName=getIntent().getStringExtra(LogisticActionAdapter.ACTION_NAME);
        listView= (ListView) findViewById(R.id.lv_org_agency);
        titleBar=(TitleBar) findViewById(R.id.org_agency_title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.choose_org_agency));
        orgAgencyList= LogisticManager.getInstance().getOrganizationAgencyList();
        adapter=new OrgAgencyAdapter(this);
        adapter.setOrgAgencyList(orgAgencyList);
        adapter.setActionId(actionId);
        adapter.setActionName(actionName);
        listView.setAdapter(adapter);
    }
}
