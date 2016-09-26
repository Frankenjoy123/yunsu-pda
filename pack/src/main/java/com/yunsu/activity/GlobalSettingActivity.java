package com.yunsu.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.sqlite.service.PatternService;

public class GlobalSettingActivity extends BaseActivity {
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

    @ViewById(id = R.id.tv_pack_pattern_value)
    private TextView tv_pack_pattern_value;

    @ViewById(id = R.id.tv_product_pattern_value)
    private TextView tv_product_pattern_value;

    @ViewById(id = R.id.et_product_regex)
    private  EditText et_product_regex;

    @ViewById(id = R.id.et_pack_regex)
    private EditText et_pack_regex;

    private final int CLEAR_SUCCESS=1;

    private PatternService patternService;

    private static final int QUERY_PACK_PATTERN_MSG=256;

    private static final int QUERY_PRODUCT_PATTERN_MSG=271;

    private static final int RESTORE_SETTING_MSG=168;

    public static final String PATTERN_ID="pattern_id";

    private static final long FALSE_SETTING= -1;

    private  PatternInfo productPatternInfo;

    private  PatternInfo packPatternInfo;

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
        et_pack_regex.setText(YunsuKeyUtil.getInstance().getPackPatternString());
        et_product_regex.setText(YunsuKeyUtil.getInstance().getProductPatternString());
    }

    @Override
    protected void onPause() {
        String productRegex=et_product_regex.getText().toString();
        String packRegex=et_pack_regex.getText().toString();

        if (!StringHelper.isStringNullOrEmpty(packRegex)){
            YunsuKeyUtil.getInstance().savePackKeyPattern(packRegex);
        }
        if (!StringHelper.isStringNullOrEmpty(productRegex)){
            YunsuKeyUtil.getInstance().saveProductKeyPattern(packRegex);
        }
        super.onPause();
    }


}
