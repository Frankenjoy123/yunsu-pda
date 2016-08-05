package com.yunsoo.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.yunsoo.adapter.LogisticActionAdapter;
import com.yunsoo.adapter.PathAdapter;
import com.yunsoo.service.ServiceExecutor;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.sqlite.SQLiteOperation;
import com.yunsoo.util.Constants;
import com.yunsoo.util.StringUtils;
import com.yunsoo.view.TitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RevokeScanActivity extends Activity {
    private TitleBar titleBar;
    private ListView lv_revoke_scan;
    private EditText et_revoke_scan;

    private List<String> keys=new ArrayList<String>();
    private PathAdapter adaper;
    private MyDataBaseHelper dataBaseHelper;
    private String actionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeal_scan);
        init();
        bindEditTextChange();
    }

    private void init() {
        getActionBar().hide();
        titleBar= (TitleBar) findViewById(R.id.revoke_scan_title_bar);
        String title=getIntent().getStringExtra(Constants.TITLE);
        if (title.equals(Constants.Logistic.REVOKE_INBOUND)){
            actionId=Constants.Logistic.INBOUND_CODE;
        }else {
            actionId=Constants.Logistic.OUTBOUND_CODE;
        }
        titleBar.setTitle(title);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        et_revoke_scan= (EditText) findViewById(R.id.et_revoke_scan);
        lv_revoke_scan=(ListView) findViewById(R.id.lv_revoke_scan);
        adaper=new PathAdapter(this, getResources());
        adaper.setKeyList(keys);
        lv_revoke_scan.setAdapter(adaper);
        dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
    }


    private void bindEditTextChange() {

        et_revoke_scan.requestFocus();
        et_revoke_scan.addTextChangedListener(new TextWatcher() {

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
                    String formatKey= StringUtils.replaceBlank(StringUtils.getLastString(string));
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

                        checkKeyStatus(formatKey);
                        keys.add(0,StringUtils.replaceBlank(StringUtils.getLastString(string)));

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

    private void checkKeyStatus(final String key){
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                final Date date=new Date();
                List<String> notSyncKeyList=SQLiteOperation.queryKey(dataBaseHelper.getReadableDatabase(),key,actionId,Constants.DB.NOT_SYNC);
                if (notSyncKeyList!=null&&notSyncKeyList.size()>0){
                    SQLiteOperation.revokePathData(dataBaseHelper.getWritableDatabase(),dateFormat.format(date),key,actionId);
                }else {
                    List<String> hasSyncKeyList=SQLiteOperation.queryKey(dataBaseHelper.getReadableDatabase(),key,actionId,Constants.DB.SYNC);
                    if (hasSyncKeyList!=null&&hasSyncKeyList.size()>0){
                        SQLiteOperation.revokePathData(dataBaseHelper.getWritableDatabase(),dateFormat.format(date),key,actionId);
                        //TODO 处理撤销逻辑,生成文件上传
                    }
                }
            }
        });
    }

}
