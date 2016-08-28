package com.yunsu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.adapter.LogisticActionAdapter;
import com.yunsu.adapter.PathAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringUtils;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Pack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathActivity extends Activity {
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	private String uniqueId;
    private String actionId;
    private String actionName;
    private String agencyId;
    private String agencyName;

    private TitleBar titleBar;
    private EditText et_path;
    private List<String> keys=new ArrayList<String>();
    private PathAdapter adaper;
    private ListView lv_path;
    private TextView tv_agency_name;
    private TextView tv_count_value;
    private PackService packService;

    @ViewById(id = R.id.tv_empty_pack_tip)
    private TextView tv_empty_pack_tip;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);
        init();
	}

    private void init() {
        packService=new PackServiceImpl();
        actionId=getIntent().getStringExtra(LogisticActionAdapter.ACTION_ID);
        actionName=getIntent().getStringExtra(LogisticActionAdapter.ACTION_NAME);
        if (actionId.equals(Constants.Logistic.INBOUND_CODE)){
            agencyId=Constants.DEFAULT_STORAGE;
            agencyName=Constants.BLANK;
        }else {
            agencyId=getIntent().getStringExtra(Constants.Logistic.AGENCY_ID);
            agencyName=getIntent().getStringExtra(Constants.Logistic.AGENCY_NAME);
        }
        tv_agency_name= (TextView) findViewById(R.id.tv_agency_name);
        tv_count_value= (TextView) findViewById(R.id.tv_count_value);
        tv_agency_name.setText(agencyName);
        tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");
        preferences=getSharedPreferences("pathActivityPre", Context.MODE_PRIVATE);
        editor=preferences.edit();

        getActionBar().hide();
        titleBar=(TitleBar) findViewById(R.id.title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setDisplayAsBack(true);
        if (actionId.equals(Constants.Logistic.INBOUND_CODE)){
            titleBar.setTitle(getString(R.string.inbound_scan));
        }else {
            titleBar.setTitle(getString(R.string.outbound_scan));
        }
        titleBar.setRightButtonText(getString(R.string.repeal_inbound));
        titleBar.setOnRightButtonClickedListener(view -> {
            Intent intent =new Intent(PathActivity.this,RevokeScanActivity.class);
            intent.putExtra(Constants.TITLE,Constants.Logistic.REVOKE_INBOUND);
            startActivity(intent);
        });
        lv_path=(ListView) findViewById(R.id.lv_path);
        adaper=new PathAdapter(this, getResources());
        adaper.setKeyList(keys);
        lv_path.setAdapter(adaper);
        lv_path.setEmptyView(tv_empty_pack_tip);
        bindTextChanged();
    }


    /**
     * 将扫描的key结果存储到DB
     * @param packKey
     */
	private void submitToDB(final String packKey) {
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Pack pack=new Pack();
                pack.setPackKey(packKey);
                pack.setStatus(Constants.DB.NOT_SYNC);
                pack.setActionId(actionId);
                pack.setAgency(agencyId);
                packService.insertPackWithCheck(pack);
            }
        });
	}


    /**
     * 扫码事件
     */
    private void bindTextChanged(){
		et_path= (EditText) findViewById(R.id.et_path);
		et_path.requestFocus();
		

		et_path.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {
                String string=new StringBuilder(s).toString();
                Pattern pattern = null;
                switch (BuildConfig.FLAVOR){
                    case Constants.Favor.KANGCAI:
                        pattern=Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
                        break;
                    default:
                        pattern=Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
                }
                Matcher matcher=pattern.matcher(string);
                if (matcher.find()){
                    String formatKey=StringUtils.replaceBlank(StringUtils.getLastString(string));
                    try {

                        if(string.isEmpty()||string=="\n"){
                            return;
                        }
                        if (keys != null && keys.size() > 0) {
                            for (int i = 0; i < keys.size(); i++) {
                                String historyFormatKey=StringUtils.getLastString(StringUtils.replaceBlank(keys.get(i)));
                                if (formatKey.equals(historyFormatKey))
                                {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            getString(R.string.key_exist) , Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER , 0, 0);
                                    toast.show();
                                    return;
                                }
                            }
                        }

                        submitToDB(formatKey);
                        keys.add(0,StringUtils.replaceBlank(StringUtils.getLastString(string)));
                        tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");
                        adaper.notifyDataSetChanged();

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.key_not_verify) , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }

			}
		});
	}

}
