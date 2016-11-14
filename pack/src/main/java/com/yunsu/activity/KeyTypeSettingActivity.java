package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.activity.BaseActivity;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.receiver.BarcodeReceiver;
import com.yunsu.sqlite.service.PatternService;

import org.json.JSONObject;

public class KeyTypeSettingActivity extends BaseActivity {
    private TitleBar titleBar;
    private Button btn_authorize_status;
    private RelativeLayout rl_auto_sync;
    private TextView tv_time_gap;
    private CheckBox cb_auto_inbound;
    private boolean autoInbound;

    @ViewById(id = R.id.tv_cache_size)
    private TextView tv_cache_size;

    @ViewById(id = R.id.rl_clear_cache)
    private RelativeLayout rl_clear_cache;


    @ViewById(id = R.id.rl_product_key_type)
    private RelativeLayout rl_product_key_type;

    @ViewById(id = R.id.rl_pack_key_type)
    private RelativeLayout rl_pack_key_type;

    @ViewById(id = R.id.tv_product_regex)
    private  TextView tv_product_regex;

    @ViewById(id = R.id.tv_pack_regex)
    private TextView tv_pack_regex;

    @ViewById(id = R.id.btn_change_setting)
    private Button btn_change_setting;

    @ViewById(id = R.id.tv_device_code)
    private TextView tv_device_code;

    private final int CLEAR_SUCCESS=1;

    private PatternService patternService;

    private static final int QUERY_PACK_PATTERN_MSG=256;

    private static final int QUERY_PRODUCT_PATTERN_MSG=271;

    private static final int RESTORE_SETTING_MSG=168;

    public static final String PATTERN_ID="pattern_id";

    private static final long FALSE_SETTING= -1;

    private  PatternInfo productPatternInfo;

    private  PatternInfo packPatternInfo;

    protected AlertDialog packAlertDialog;

    private ScanBarcodeReceiver mReceiver=new ScanBarcodeReceiver();
    private EditText et_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_type_setting);
        init();
    }


    private class ScanBarcodeReceiver extends BarcodeReceiver {

        @Override
        protected void doWithReceiver(Intent intent) {
            String string = intent.getStringExtra(Constants.KEY_BARCODE_STR);
            dealWithScanContent(string);
        }
    }

    private void init() {
        getActionBar().hide();

        titleBar = (TitleBar) findViewById(R.id.global_setting_title_bar);
        titleBar.setTitle(getString(R.string.set_key_type));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        tv_pack_regex.setText(YunsuKeyUtil.getInstance().getPackPatternString());
        tv_product_regex.setText(YunsuKeyUtil.getInstance().getProductPatternString());
        tv_device_code.setText(DeviceManager.getInstance().getDeviceId());

        btn_change_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register receiver
                IntentFilter filter = new IntentFilter();
                filter.addAction(Constants.ACTION_BARCODE_SERVICE_BROADCAST);
                registerReceiver(mReceiver, filter);

                showUpdateDialog();
            }
        });
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_setting, null);
        et_key = (EditText) view.findViewById(R.id.et_key);
        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                    String string = et_key.getText().toString();
                    if (StringHelper.isStringNullOrEmpty(string)){
                        return;
                    }
                dealWithScanContent(string);

            }
        });

        builder.setView(view);
        builder.setPositiveButton(R.string.cancel, null);
        packAlertDialog = builder.create();
        packAlertDialog.show();

    }

    private void dealWithScanContent(String string){
        try {
            JSONObject object=new JSONObject(string);
            String pack_pattern=object.getString("p");
            String prod_pattern=object.getString("pr");
            if (!StringHelper.isStringNullOrEmpty(pack_pattern)){
                YunsuKeyUtil.getInstance().savePackKeyPattern(pack_pattern);
                tv_pack_regex.setText(pack_pattern);
            }
            if (!StringHelper.isStringNullOrEmpty(prod_pattern)){
                YunsuKeyUtil.getInstance().saveProductKeyPattern(prod_pattern);
                tv_product_regex.setText(prod_pattern);
            }
            ToastMessageHelper.showMessage(KeyTypeSettingActivity.this,R.string.update_success,true);
            unregisterReceiver(mReceiver);
            packAlertDialog.dismiss();

        } catch (Exception e) {
            ToastMessageHelper.showMessage(KeyTypeSettingActivity.this,R.string.update_wrong,true);
            e.printStackTrace();
        }finally {
            et_key.setText("");
        }
    }

    @Override
    protected void onPause() {

        String productRegex= tv_product_regex.getText().toString();
        String packRegex= tv_pack_regex.getText().toString();

        if (!StringHelper.isStringNullOrEmpty(packRegex)){
            YunsuKeyUtil.getInstance().savePackKeyPattern(packRegex);
        }
        if (!StringHelper.isStringNullOrEmpty(productRegex)){
            YunsuKeyUtil.getInstance().saveProductKeyPattern(productRegex);
        }
        super.onPause();
    }

}
