package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.sqlite.service.PatternService;

import org.json.JSONObject;

public class GlobalSettingActivity extends BaseActivity {
    private TitleBar titleBar;
    private Button btn_authorize_status;
    private RelativeLayout rl_auto_sync;
    private TextView tv_time_gap;
    private CheckBox cb_auto_inbound;
    private boolean autoInbound;

    @ViewById(id = R.id.rl_sync_data)
    private RelativeLayout rl_sync_data;

    @ViewById(id = R.id.rl_set_key_type)
    private RelativeLayout rl_set_key_type;

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

    @ViewById(id = R.id.tv_current_version)
    private TextView tv_current_version;

    @ViewById(id = R.id.rl_pack_setting)
    private RelativeLayout rl_pack_setting;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_setting);
        init();
    }

    private void init() {
        getActionBar().hide();

        titleBar = (TitleBar) findViewById(R.id.global_setting_title_bar);
        titleBar.setTitle(getString(R.string.settings));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        tv_pack_regex.setText(YunsuKeyUtil.getInstance().getPackPatternString());
        tv_product_regex.setText(YunsuKeyUtil.getInstance().getProductPatternString());
        tv_device_code.setText(DeviceManager.getInstance().getDeviceId());
        tv_current_version.setText(BuildConfig.VERSION_NAME);

        btn_change_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog();
            }
        });

        bindClickEvent();
    }

    private void bindClickEvent() {
        rl_sync_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GlobalSettingActivity.this,SyncDataActivity.class);
                startActivity(intent);
            }
        });

        rl_set_key_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GlobalSettingActivity.this,KeyTypeSettingActivity.class);
                startActivity(intent);
            }
        });

        rl_pack_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(GlobalSettingActivity.this,PackSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_setting, null);
        final EditText et_key = (EditText) view.findViewById(R.id.et_key);
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
                        ToastMessageHelper.showMessage(GlobalSettingActivity.this,R.string.update_success,true);
                        packAlertDialog.dismiss();

                    } catch (Exception e) {
                        ToastMessageHelper.showMessage(GlobalSettingActivity.this,R.string.update_wrong,true);
                        e.printStackTrace();
                    }finally {
                        et_key.setText("");
                    }
            }
        });

        builder.setView(view);
        builder.setPositiveButton(R.string.cancel, null);
        packAlertDialog = builder.create();
        packAlertDialog.show();

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
