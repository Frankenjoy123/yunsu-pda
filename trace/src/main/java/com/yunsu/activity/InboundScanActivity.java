package com.yunsu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.adapter.PathAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InboundScanActivity extends BaseActivity {
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

    private String actionId;

    private String agencyId;
    private String agencyName;

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_path)
    private EditText et_path;

    private List<String> keys=new ArrayList<String>();
    private PathAdapter adaper;

    @ViewById(id = R.id.lv_path)
    private ListView lv_path;

    @ViewById(id = R.id.tv_agency_name)
    private TextView tv_agency_name;

    @ViewById(id = R.id.tv_count_value)
    private TextView tv_count_value;

    private PackService packService;

    @ViewById(id = R.id.tv_empty_pack_tip)
    private TextView tv_empty_pack_tip;

    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final int INSERT_PACK_SUCCESS=200;

    private static final int EXIST_MSG=123;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);
        init();
	}

    private void init() {
        packService=new PackServiceImpl();
        actionId=Constants.Logistic.INBOUND_CODE;
        agencyId=Constants.DEFAULT_STORAGE;
        agencyName=Constants.BLANK;

        tv_agency_name.setText(agencyName);
        tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");
        preferences=getSharedPreferences("pathActivityPre", Context.MODE_PRIVATE);
        editor=preferences.edit();

        getActionBar().hide();

        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.inbound_scan));
        titleBar.setRightButtonText(getString(R.string.repeal_inbound));
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(InboundScanActivity.this,RevokeScanActivity.class);
                intent.putExtra(Constants.TITLE,Constants.Logistic.REVOKE_INBOUND);
                startActivity(intent);
            }
        });
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
                Pack queryPack=new Pack();
                queryPack.setPackKey(packKey);
                queryPack.setActionId(Constants.Logistic.INBOUND_CODE);
                Pack resultPack=packService.queryRevokeOrNot(queryPack);
                //已入库或撤销入库
                if (resultPack!=null){
                    //已撤销入库
                    if (resultPack.getActionId().equals(Constants.Logistic.REVOKE_INBOUND_CODE)){
                        resultPack.setActionId(Constants.Logistic.INBOUND_CODE);
                        resultPack.setStatus(Constants.DB.NOT_SYNC);
                        resultPack.setSaveTime(dateFormat.format(new Date()));
                        packService.updatePack(resultPack);
                        keys.add(packKey);
                        handler.sendEmptyMessage(INSERT_PACK_SUCCESS);
                    }else {//已入库
                        handler.sendEmptyMessage(EXIST_MSG);
                    }
                }else {//未入库
                    Pack pack=new Pack();
                    pack.setPackKey(packKey);
                    pack.setStatus(Constants.DB.NOT_SYNC);
                    pack.setActionId(Constants.Logistic.INBOUND_CODE);
                    pack.setAgency(Constants.DEFAULT_STORAGE);
                    pack.setSaveTime(dateFormat.format(new Date()));
                    packService.insertPackData(pack);
                    keys.add(packKey);
                    handler.sendEmptyMessage(INSERT_PACK_SUCCESS);
                }

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
                if (StringHelper.isStringNullOrEmpty(string)){
                    return;
                }

                try {
                    String formatKey=YunsuKeyUtil.getInstance().verifyPackageKey(string);
                    submitToDB(formatKey);

                } catch (NotVerifyException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage() , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }finally {
                    et_path.setText("");
                }
			}
		});
	}


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case INSERT_PACK_SUCCESS:
                    adaper.notifyDataSetChanged();
//                    keys.add(0,StringUtils.replaceBlank(StringUtils.getLastString(string)));
                    tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");
//                    adaper.notifyDataSetChanged();
                    break;

                case EXIST_MSG:
                    ToastMessageHelper.showMessage(InboundScanActivity.this,
                            R.string.key_inbound_already,true);
                    break;
            }
        }
    };

}
