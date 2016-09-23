package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.KeyPatternAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.sqlite.service.PatternService;
import com.yunsu.sqlite.service.impl.PatternServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class KeyTempleListActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_temple)
    ListView lv_temple;

    @ViewById(id = R.id.tv_empty_staff_tip)
    private TextView tv_empty_staff_tip;

    private KeyPatternAdapter keyPatternAdapter;

    private PatternService patternService;

    private static final  int QUERY_ALL_PATTERN_MSG =134;

    private static final int DELETE_PATTERN_MSG =104;

    public static final int CREATE_NEW_STAFF_REQUEST=205;

    public static final int CREATE_NEW_STAFF_RESULT=207;

    private List<PatternInfo> patternInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_temple_list);
        init();
    }


    private void init() {
        getActionBar().hide();

        patternInfoList =new ArrayList<>();

        titleBar.setTitle(getString(R.string.staff_list));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.create));
//        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(StaffListActivity.this,CreateStaffActivity.class);
//                startActivityForResult(intent,CREATE_NEW_STAFF_REQUEST);
//            }
//        });
        keyPatternAdapter =new KeyPatternAdapter(this);
        keyPatternAdapter.setPatternInfoList(patternInfoList);

        lv_temple.setAdapter(keyPatternAdapter);

        patternService =new PatternServiceImpl();

        lv_temple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=getIntent();
                intent.putExtra(PackSettingActivity.STAFF_ID, patternInfoList.get(i).getId());
                setResult(PackSettingActivity.STAFF_RESULT,intent);
                finish();
            }
        });
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
                    PatternInfo patternInfo=new PatternInfo(null,"云溯官方码",s1);
                    patternService.insert(patternInfo);
                    String s2="^https?://ws.oyao.com/fw\\?f=([^\\/]+)$";
                    PatternInfo patternInfo2=new PatternInfo(null,"氧泡泡官方码",s2);
                    patternService.insert(patternInfo2);
                    String s3="^(\\d+)$";
                    PatternInfo patternInfo3=new PatternInfo(null,"纯数字",s3);
                    patternService.insert(patternInfo3);
                    String s4="^([a-zA-Z\\d]+)$";
                    PatternInfo patternInfo4=new PatternInfo(null,"纯数字+字母",s4);
                    patternService.insert(patternInfo4);
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
