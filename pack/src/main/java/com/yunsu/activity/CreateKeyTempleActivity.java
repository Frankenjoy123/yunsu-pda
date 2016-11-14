package com.yunsu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.yunsu.common.activity.BaseActivity;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.sqlite.service.PatternService;
import com.yunsu.sqlite.service.impl.PatternServiceImpl;

public class CreateKeyTempleActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_temple_name)
    private EditText et_temple_name;

    @ViewById(id = R.id.et_key_example)
    private EditText et_key_example;

    @ViewById(id = R.id.et_key_prefix)
    private EditText et_key_prefix;

    @ViewById(id = R.id.btn_create_temple)
    private Button btn_create_temple;

    @ViewById(id = R.id.rl_root_create_temple)
    private RelativeLayout rl_root_create_temple;

    private PatternService patternService =new PatternServiceImpl();

    private static final int INSERT_NEW_MSG=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_key_temple);
        init();
    }



    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.create_key_temple));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);

        btn_create_temple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String templeName= et_temple_name.getText().toString();
                final String keyExample= et_key_example.getText().toString();
                final String keyPrefix= et_key_prefix.getText().toString();
                if (StringHelper.isStringNullOrEmpty(templeName)){
                    ToastMessageHelper.showErrorMessage(CreateKeyTempleActivity.this,R.string.temple_name_not_null
                            ,true);
                }else if (StringHelper.isStringNullOrEmpty(keyExample)){
                    ToastMessageHelper.showErrorMessage(CreateKeyTempleActivity.this,R.string.key_example_not_null,true);
                } else if (StringHelper.isStringNullOrEmpty(keyPrefix)){
                    ToastMessageHelper.showErrorMessage(CreateKeyTempleActivity.this,R.string.key_prefix_not_null,true);
                }else {
                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder builder=new StringBuilder("^");
                            builder.append(keyPrefix);
                            builder.append("([^\\/]+)");
                            builder.append("$");
                            PatternInfo patternInfo=new PatternInfo(null,templeName,builder.toString(),keyExample);
                            long id= patternService.insert(patternInfo);
                            Message message=new Message();
                            message.what=INSERT_NEW_MSG;
                            message.obj=id;
                            handler.sendMessage(message);
                        }
                    });

                }
            }
        });

        rl_root_create_temple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==INSERT_NEW_MSG){
                Intent intent=getIntent();
                intent.putExtra(Constants.PackPreference.PATTERN_ID,(long)msg.obj);
                setResult(KeyTemplateListActivity.CREATE_NEW_TEMPLE_RESULT,intent);
                finish();
            }
        }
    };

}
