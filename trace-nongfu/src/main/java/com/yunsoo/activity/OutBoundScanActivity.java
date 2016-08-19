package com.yunsu.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.adapter.LogisticActionAdapter;
import com.yunsu.adapter.PathAdapter;
import com.yunsu.annotation.ViewById;
import com.yunsu.entity.MaterialEntity;
import com.yunsu.service.ServiceExecutor;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.util.Constants;
import com.yunsu.util.StringUtils;
import com.yunsu.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Pack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutBoundScanActivity extends BaseActivity {
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_path)
    private EditText et_path;

    @ViewById(id = R.id.tv_product_name)
    private TextView tv_product_name;

    @ViewById(id = R.id.tv_amount)
    private TextView tv_amount;

    @ViewById(id = R.id.tv_send_count)
    private TextView tv_send_count;

    @ViewById(id = R.id.tv_remain_count)
    private TextView tv_remain_count;

    @ViewById(id = R.id.tv_customer_name)
    private TextView tv_customer_name;

//    @ViewById(id = R.id.tv_contact_name)
//    private TextView tv_contact_name;

//    @ViewById(id = R.id.tv_contact_number)
//    private TextView tv_contact_number;


    private List<String> keys=new ArrayList<String>();

    private PackService packService;

    private static final int QUERY_MATERIAL_SUCCESS=1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);
        init();
	}

    private void init() {
        getActionBar().hide();
        titleBar=(TitleBar) findViewById(R.id.title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.outbound_scan));

        packService=new PackServiceImpl();
        Intent intent=getIntent();
        final long materialId= intent.getLongExtra("materialId",0);
        if (materialId!=0){
            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    MaterialService materialService=new MaterialServiceImpl();
                    Material material=materialService.queryById(materialId);
                    Message message=Message.obtain();
                    message.what=QUERY_MATERIAL_SUCCESS;
                    message.obj=material;
                    handler.sendMessage(message);
                }
            });
        }


//        bindTextChanged();

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_MATERIAL_SUCCESS:
                    Material material= (Material) msg.obj;
                    tv_product_name.setText(material.getHeadSize()+"头"+material.getLevel()+"星");
                    tv_amount.setText(String.valueOf(material.getAmount()));
                    break;
            }


        }
    };


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
//                pack.setActionId(actionId);
//                pack.setAgency(agencyId);
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
//                        tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");
//                        adaper.notifyDataSetChanged();
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
