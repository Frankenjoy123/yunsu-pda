package com.yunsu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunsu.annotation.ViewById;
//import com.itxiaowu.manager.DeviceGeoLocationManager;
import com.yunsu.exception.BaseException;
import com.yunsu.manager.DeviceManager;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.LogisticManager;
import com.yunsu.manager.SQLiteManager;
import com.yunsu.manager.SessionManager;
import com.yunsu.network.CacheService;
import com.yunsu.network.NetworkManager;
import com.yunsu.service.DataServiceImpl;
import com.yunsu.service.PermanentTokenLoginService;
import com.yunsu.util.DensityUtil;

import org.json.JSONObject;


public class WelcomeActivity extends BaseActivity{

    @ViewById(id = R.id.iv_icon)
    ImageView iv_icon;

    @ViewById(id = R.id.ll_btn_area)
    View ll_btn_area;

    private boolean isAuthorize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
        isAuthorize=preferences.getBoolean("isAuthorize",false);
        init();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(isAuthorize){
                    gotoMainActivity();
                }
                else{
                    gotoAuthorizeActivity();
                }
            }
        }, 1000);

    }


    private void init() {
        int[] size = DensityUtil.getScreenHeightAndWidth(this);
        int imageSize = (int) (size[0] * 0.5);//width * 0.5

        ViewGroup.LayoutParams params = iv_icon.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        iv_icon.setLayoutParams(params);

        ll_btn_area.setVisibility(View.GONE);
    }
    private void gotoAuthorizeActivity() {
        Intent intent=new Intent(WelcomeActivity.this, AuthorizeActivity.class);
        startActivity(intent);
        finish();
    }
    private void gotoMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
