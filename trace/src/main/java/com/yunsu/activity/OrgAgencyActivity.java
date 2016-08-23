package com.yunsu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.yunsu.adapter.LogisticActionAdapter;
import com.yunsu.adapter.OrgAgencySearchAdapter;
import com.yunsu.adapter.SearchAdapter;
import com.yunsu.common.util.Constants;
import com.yunsu.entity.OrgAgency;
import com.yunsu.manager.LogisticManager;
import com.yunsu.common.view.TitleBar;

import java.util.List;
import java.util.Objects;

public class OrgAgencyActivity extends Activity {
    private TitleBar titleBar;
    private ListView listView;
    private EditText editText;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_agency);
        init();
    }

    private void init(){
        getActionBar().hide();
        intent=getIntent();
        String actionId = intent.getStringExtra(LogisticActionAdapter.ACTION_ID);
        String actionName = intent.getStringExtra(LogisticActionAdapter.ACTION_NAME);
        listView= (ListView) findViewById(R.id.lv_org_agency);
        titleBar=(TitleBar) findViewById(R.id.org_agency_title_bar);
        editText= (EditText) findViewById(R.id.search_edit_text);

        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.choose_org_agency));
        List<OrgAgency> agencies = LogisticManager.getInstance().getAgencies();

        final SearchAdapter adapter = new OrgAgencySearchAdapter(agencies, this).registerFilter(OrgAgency.class, "name")
                .setIgnoreCase(true);
        ((OrgAgencySearchAdapter)adapter).setActionId(actionId);
        ((OrgAgencySearchAdapter)adapter).setActionName(actionName);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
