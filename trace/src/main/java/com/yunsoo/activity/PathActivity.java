package com.yunsoo.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.adapter.PathAdapter;
import com.yunsoo.fileOpreation.FileOperation;
import com.yunsoo.service.ServiceExecutor;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.sqlite.SQLiteOperation;
import com.yunsoo.util.Constants;
import com.yunsoo.util.StringUtils;
import com.yunsoo.view.TitleBar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PathActivity extends Activity {
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	private String uniqueId;
    
    TitleBar titleBar;
    EditText et_path;
    List<String> keys=new ArrayList<String>();
    PathAdapter adaper;
    ListView lv_path;
	Builder builder;
	
	private boolean isFirstWrite=true;
	private String prevFileName;
	
	private MyDataBaseHelper dataBaseHelper;

    private String actionId;
    private String actionName;
    private String agencyId;
    private String agencyName;

    private TextView tv_agency_name;
    private TextView tv_count_value;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);
        init();
	}

    private void init() {
        actionId=getIntent().getStringExtra(LogisticActionAdapter.ACTION_ID);
        actionName=getIntent().getStringExtra(LogisticActionAdapter.ACTION_NAME);
        agencyId=getIntent().getStringExtra(Constants.Logistic.AGENCY_ID);
        agencyName=getIntent().getStringExtra(Constants.Logistic.AGENCY_NAME);

        tv_agency_name= (TextView) findViewById(R.id.tv_agency_name);
        tv_count_value= (TextView) findViewById(R.id.tv_count_value);
        tv_agency_name.setText(agencyName);
        tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");

        dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
        preferences=getSharedPreferences("pathActivityPre", Context.MODE_PRIVATE);
        editor=preferences.edit();
        prevFileName=preferences.getString("prevFileName", "");

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        uniqueId = deviceUuid.toString();

        getActionBar().hide();
        titleBar=(TitleBar) findViewById(R.id.title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(actionName+"扫描");

        lv_path=(ListView) findViewById(R.id.lv_path);
        adaper=new PathAdapter(this, getResources());
        adaper.setKeyList(keys);

        lv_path.setAdapter(adaper);
        bindTextChanged();

    }



	private void submitToDB(final String packKey) {
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                final Date date=new Date();

                SQLiteOperation.insertPathData(dataBaseHelper.getWritableDatabase(),
                        packKey,actionId,agencyId,Constants.DB.NOT_SYNC,dateFormat.format(date));
            }
        });
	}

    @Override
    protected void onPause() {

        editor.putString("prevFileName", prevFileName);
        editor.commit();
        dataBaseHelper.close();
        super.onPause();
    }

    @Override
    protected void onStop() {
        dataBaseHelper.close();
        super.onStop();
    }

    @Override
    public void finish() {
        dataBaseHelper.close();
        super.finish();
    }

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
                    keys.add(StringUtils.replaceBlank(StringUtils.getLastString(string)));
                    tv_count_value.setText("已扫"+String.valueOf(keys.size())+"包");
                    adaper.notifyDataSetChanged();
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		});
	}

}
