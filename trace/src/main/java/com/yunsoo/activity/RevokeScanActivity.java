package com.yunsoo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.yunsoo.adapter.PathAdapter;
import com.yunsoo.service.ServiceExecutor;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.sqlite.service.PackService;
import com.yunsoo.sqlite.service.impl.PackServiceImpl;
import com.yunsoo.util.Constants;
import com.yunsoo.util.StringUtils;
import com.yunsoo.util.ToastMessageHelper;
import com.yunsoo.view.TitleBar;
import com.yunsu.greendao.entity.Pack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RevokeScanActivity extends Activity {
    private TitleBar titleBar;
    private ListView lv_revoke_scan;
    private EditText et_revoke_scan;

    private List<String> keys=new ArrayList<String>();
    private PathAdapter adapter;
    private String actionId;
    private String actionName;
    private String title;
    private PackService packService;

    private final int SUCCESS_MSG=1;
    private final int FAIL_MSG=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeal_scan);
        init();
        bindEditTextChange();
    }

    private void init() {
        packService=new PackServiceImpl();

        getActionBar().hide();
        titleBar= (TitleBar) findViewById(R.id.revoke_scan_title_bar);
        title=getIntent().getStringExtra(Constants.TITLE);
        if (title.equals(Constants.Logistic.REVOKE_INBOUND)){
            actionId=Constants.Logistic.INBOUND_CODE;
            actionName=Constants.Logistic.INBOUND;
        }else {
            actionId=Constants.Logistic.OUTBOUND_CODE;
            actionName=Constants.Logistic.OUTBOUND;
        }
        titleBar.setTitle(title);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        et_revoke_scan= (EditText) findViewById(R.id.et_revoke_scan);
        lv_revoke_scan=(ListView) findViewById(R.id.lv_revoke_scan);
        adapter =new PathAdapter(this, getResources());
        adapter.setKeyList(keys);
        lv_revoke_scan.setAdapter(adapter);

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_MSG:
                    if (((Pack)msg.obj).getStatus().equals(Constants.DB.DISABLE)){
                        ToastMessageHelper.showMessage(RevokeScanActivity.this,"当前包装已被"+title+"，请检查",true);
                    }else {
                        keys.add(0,((Pack)msg.obj).getPackKey());
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case FAIL_MSG:
                    ToastMessageHelper.showMessage(RevokeScanActivity.this,"当前包装还未"+actionName+"，请检查",true);
            }
            super.handleMessage(msg);
        }
    };


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
                Pack queryPack=new Pack();
                queryPack.setPackKey(key);
                queryPack.setActionId(actionId);
                Pack resultPack=packService.queryByKeyAction(queryPack);

                if (resultPack!=null){
                    Pack messagePack=new Pack();
                    messagePack.setPackKey(resultPack.getPackKey());
                    messagePack.setStatus(resultPack.getStatus());

                    if (!resultPack.getStatus().equals(Constants.DB.DISABLE)){
                        packService.revokePathData(resultPack);
                    }

                    Message message=Message.obtain();
                    message.what=SUCCESS_MSG;
                    message.obj=messagePack;
                    handler.sendMessage(message);
                    //TODO 如果是已经同步的，处理撤销逻辑,重新生成文件上传
                }else {
                    //TODO UI返回扫描不存在
                    Message message=Message.obtain();
                    message.what=FAIL_MSG;
                    handler.sendMessage(message);
                }
            }
        });
    }

}
