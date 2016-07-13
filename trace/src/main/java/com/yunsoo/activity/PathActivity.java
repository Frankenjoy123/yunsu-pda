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
import android.widget.Toast;

import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.adapter.PathAdapter;
import com.yunsoo.fileOpreation.FileOperation;
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
    List<String> keys;
    PathAdapter adaper;
    ListView lv_path;
	Builder builder;
	Button btnSubmit;
	
	private boolean isFirstWrite=true;
	private String prevFileName;
	private Button btnPathHistory;
	
	private MyDataBaseHelper dataBaseHelper;

    private int actionId;
    private String actionName;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);
        init();
	}

    private void init() {

        actionId=getIntent().getIntExtra(LogisticActionAdapter.ACTION_ID,0);
        actionName=getIntent().getStringExtra(LogisticActionAdapter.ACTION_NAME);

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
        keys=new ArrayList<String>();
        titleBar=(TitleBar) findViewById(R.id.title_bar);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(actionName+"扫描");

        btnSubmit=(Button) findViewById(R.id.btn_submitPath);
        if(keys==null|| keys.size()==0){
            btnSubmit.setEnabled(false);
        }
        else {
            btnSubmit.setEnabled(false);
        }
        bindSubmit();
        lv_path=(ListView) findViewById(R.id.lv_path);
        adaper=new PathAdapter(this, getResources());
        adaper.setKeyList(keys);

        lv_path.setAdapter(adaper);
        bindTextChanged();

        btnPathHistory=(Button) findViewById(R.id.btn_pathHistory);
        btnPathHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent=new Intent(PathActivity.this,PathHistoryActivity.class);
                intent.putExtra(LogisticActionAdapter.ACTION_NAME,actionName);
                intent.putExtra(LogisticActionAdapter.ACTION_ID,actionId);
                startActivity(intent);
            }
        });
    }


    @Override
	protected void onPause() {

		editor.putString("prevFileName", prevFileName);
		editor.commit();
		super.onPause();
	}
	
	
	
	private void bindSubmit() {

        final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final Date date=new Date();
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!keys.isEmpty()){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < keys.size(); i++) {
                                SQLiteOperation.insertPathData(dataBaseHelper.getWritableDatabase(),
                                        keys.get(i),actionId,dateFormat.format(date));
                            }
                            keys.clear();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnSubmit.setEnabled(false);
                                    adaper.notifyDataSetChanged();
                                }
                            });

                        }
                    }).start();
				}
			}
		});
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
				try {

					if(string.isEmpty()||string=="\n"){
						return;
					}
                    if (keys != null && keys.size() > 0) {
                        for (int i = 0; i < keys.size(); i++) {
                            if (StringUtils.replaceBlank(StringUtils.getLastString(string))
                            		.equals(StringUtils.getLastString(StringUtils.replaceBlank(keys.get(i))))) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                       getString(R.string.key_exist) , Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER , 0, 0);
                                toast.show();
                                return;
                            }
                        }
                    }
                    keys.add(StringUtils.replaceBlank(StringUtils.getLastString(string)));
                    if (keys==null||keys.size()==0){
                        btnSubmit.setEnabled(false);
                    }
                    else {
                        btnSubmit.setEnabled(true);
                    }

                    adaper.notifyDataSetChanged();
					
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		});
	}

}
