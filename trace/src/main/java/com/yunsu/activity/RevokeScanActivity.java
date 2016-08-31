package com.yunsu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.adapter.PathAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringUtils;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Pack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RevokeScanActivity extends BaseActivity {
    private TitleBar titleBar;
    private ListView lv_revoke_scan;
    private EditText et_revoke_scan;

    @ViewById(id = R.id.tv_empty_key_tip)
    private TextView tv_empty_key_tip;

    private List<String> keys=new ArrayList<String>();
    private PathAdapter adapter;
    private String actionId;
    private String revokeActionId;
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
            revokeActionId=Constants.Logistic.REVOKE_INBOUND_CODE;
            actionName=Constants.Logistic.INBOUND;
        }else {
            actionId=Constants.Logistic.OUTBOUND_CODE;
            revokeActionId=Constants.Logistic.REVOKE_OUTBOUND_CODE;
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
        lv_revoke_scan.setEmptyView(tv_empty_key_tip);
    }

    /**
     * 扫码事件触发
     */
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
                try {
                    String formatKey= YunsuKeyUtil.verifyScanKey(string);
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


                } catch (NotVerifyException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage() , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }

            }
        });

    }

    /**
     * 检查码的状态
     * @param key
     */
    private void checkKeyStatus(final String key){
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Pack queryPack=new Pack();
                queryPack.setPackKey(key);
                queryPack.setActionId(actionId);
                Pack resultPack=packService.queryRevokeOrNot(queryPack);

                if (resultPack!=null){
                    Pack messagePack=new Pack();
                    messagePack.setPackKey(resultPack.getPackKey());
                    messagePack.setActionId(resultPack.getActionId());

                    if (resultPack.getActionId().equals(Constants.Logistic.INBOUND_CODE)||
                            resultPack.getActionId().equals(Constants.Logistic.OUTBOUND_CODE))
                        packService.revokePathData(resultPack);

                    Message message=Message.obtain();
                    message.what=SUCCESS_MSG;
                    message.obj=messagePack;
                    handler.sendMessage(message);

                }else {
                    //TODO UI返回扫描不存在
                    Message message=Message.obtain();
                    message.what=FAIL_MSG;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what==SUCCESS_MSG){
                switch (((Pack)msg.obj).getActionId()){
                    case  Constants.Logistic.REVOKE_INBOUND_CODE :
                    case Constants.Logistic.REVOKE_OUTBOUND_CODE:
                        ToastMessageHelper.showMessage(RevokeScanActivity.this,"当前包装已被"+title+"，请检查",true);
                        break;

                    case Constants.Logistic.INBOUND_CODE:
                    case Constants.Logistic.OUTBOUND_CODE:
                        keys.add(0,((Pack)msg.obj).getPackKey());
                        adapter.notifyDataSetChanged();
                        break;
                }

            }
            else if (msg.what==FAIL_MSG){
                ToastMessageHelper.showMessage(RevokeScanActivity.this,"当前包装还未"+actionName+"，请检查",true);
            }
            super.handleMessage(msg);
        }
    };

}
