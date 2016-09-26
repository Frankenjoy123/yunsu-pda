package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yunsu.adapter.KeyPatternAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.sqlite.service.PatternService;
import com.yunsu.sqlite.service.impl.PatternServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class KeyTemplateListActivity extends BaseActivity {

    @ViewById(id = R.id.temple_title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_temple)
    ListView lv_temple;

    private KeyPatternAdapter keyPatternAdapter;

    private PatternService patternService;

    private static final  int QUERY_ALL_PATTERN_MSG =134;

    private List<PatternInfo> patternInfoList;

    public static final int PACK_REQUEST=303;

    public static final int PRODUCT_REQUEST=306;

    public static final int PATTERN_RESULT=307;

    public static final int CREATE_NEW_TEMPLE_REQUEST=205;

    public static final int CREATE_NEW_TEMPLE_RESULT=207;

    private String patternType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_temple_list);
        init();
    }

    private void init() {
        getActionBar().hide();

        patternInfoList =new ArrayList<>();

        patternType=getIntent().getStringExtra(Constants.PackPreference.PATTERN);

        titleBar.setTitle(getString(R.string.temple_list));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.create));
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(KeyTemplateListActivity.this,CreateKeyTempleActivity.class);
                startActivityForResult(intent,CREATE_NEW_TEMPLE_REQUEST);
            }
        });

        keyPatternAdapter =new KeyPatternAdapter(this);
        keyPatternAdapter.setPatternInfoList(patternInfoList);

        lv_temple.setAdapter(keyPatternAdapter);

        patternService =new PatternServiceImpl();

        lv_temple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (patternType.equals(Constants.PackPreference.PACK_PATTERN)){
                    YunsuKeyUtil.getInstance().storePackKeyPattern(patternInfoList.get(i).getRegex());
                }else {
                    YunsuKeyUtil.getInstance().storeProductKeyPattern(patternInfoList.get(i).getRegex());
                }

                Intent intent=getIntent();
                intent.putExtra(GlobalSettingActivity.PATTERN_ID, patternInfoList.get(i).getId());
                setResult(PATTERN_RESULT,intent);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==CREATE_NEW_TEMPLE_REQUEST && resultCode==CREATE_NEW_TEMPLE_RESULT){
            Intent intent=getIntent();
            intent.putExtra(GlobalSettingActivity.PATTERN_ID,data.getLongExtra(Constants.PackPreference.PATTERN_ID,0));
            setResult(PATTERN_RESULT,intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {

        showLoading();
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                patternInfoList.clear();
                List<PatternInfo> tempPatternList=patternService.queryAllPatternInfo();
                if (tempPatternList.size()>0){
                    patternInfoList.addAll(tempPatternList);
                }else {
                    String s1="^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$";
                    String example1="http://dev.yunsu.co:7080/fBC8IFwlR9K65E87Udt86x";
                    PatternInfo patternInfo=new PatternInfo(null,"云溯官方码",s1,example1);
                    patternService.insert(patternInfo);
                    String s2="^https?://ws.oyao.com/fw\\?f=([^\\/]+)$";
                    String example2="http://ws.oyao.com/fw?=6569340860261171";
                    PatternInfo patternInfo2=new PatternInfo(null,"氧泡泡官方码",s2,example2);
                    patternService.insert(patternInfo2);
                    String s3="^(\\d+)$";
                    PatternInfo patternInfo3=new PatternInfo(null,"纯数字",s3,"12345678");
                    patternService.insert(patternInfo3);
                    String s4="^([a-zA-Z\\d]+)$";
                    PatternInfo patternInfo4=new PatternInfo(null,"纯数字+字母",s4,"fBC8IFwlR9K65E87Udt86x");
                    patternService.insert(patternInfo4);
                    tempPatternList=patternService.queryAllPatternInfo();
                    patternInfoList.addAll(tempPatternList);
                }

                handler.sendEmptyMessage(QUERY_ALL_PATTERN_MSG);

            }
        });

        super.onResume();
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_ALL_PATTERN_MSG:
                    keyPatternAdapter.notifyDataSetChanged();
                    hideLoading();
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };


}
