package com.yunsu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunsu.common.activity.BaseActivity;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.DensityUtil;


public class WelcomeActivity extends BaseActivity {

    @ViewById(id = R.id.iv_icon)
    ImageView iv_icon;

    @ViewById(id = R.id.ll_btn_area)
    View ll_btn_area;

    private boolean isAuthorize;

    private static final  String TAG="WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences preferences=getSharedPreferences(Constants.Preference.YUNSU_PDA,MODE_PRIVATE);
        isAuthorize=preferences.getBoolean(Constants.Preference.IS_AUTHORIZE,false);
        init();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(isAuthorize){
                    gotoPathMainActivity();
                }
                else{
                    gotoAuthorizeActivity();
                }
            }
        }, 1000);

    }


    /**
     * 欢迎界面初始化
     */
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
        Intent intent=new Intent(WelcomeActivity.this, AuthorizeActivityImpl.class);
        startActivity(intent);
        finish();
    }

    private void gotoPathMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, PathMainActivity.class);
        startActivity(intent);
        finish();
    }

}
