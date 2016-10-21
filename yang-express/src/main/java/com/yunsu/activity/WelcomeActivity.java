package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.util.DensityUtil;

//import com.itxiaowu.manager.DeviceGeoLocationManager;


public class WelcomeActivity extends BaseActivity {

    @ViewById(id = R.id.iv_icon)
    ImageView iv_icon;

    @ViewById(id = R.id.ll_btn_area)
    View ll_btn_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                    gotoMainActivity();
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
        Intent intent = new Intent(WelcomeActivity.this, PackMainActivity.class);
        startActivity(intent);
        finish();
    }

}
