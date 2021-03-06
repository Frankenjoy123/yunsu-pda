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
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.DensityUtil;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.sqlite.service.ProductBaseService;
import com.yunsu.sqlite.service.StaffService;
import com.yunsu.sqlite.service.impl.ProductBaseServiceImpl;
import com.yunsu.sqlite.service.impl.StaffServiceImpl;

import java.util.List;

//import com.itxiaowu.manager.DeviceGeoLocationManager;


public class WelcomeActivity extends BaseActivity {

    @ViewById(id = R.id.iv_icon)
    ImageView iv_icon;

    @ViewById(id = R.id.ll_btn_area)
    View ll_btn_area;

    private boolean isAuthorize;

    private StaffService staffService;

    private ProductBaseService productBaseService;

    private static final String PREF_SHORTCUT = "Short_CUT";
    private static final String KEY_SHORTCUT = "key_has_shortcut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        staffService=new StaffServiceImpl();

        productBaseService=new ProductBaseServiceImpl();

        SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
        isAuthorize=preferences.getBoolean("isAuthorize",false);
        init();

        initDefaultPackSetting();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isAuthorize){
                    gotoMainActivity();
                }else {
                    gotoAuthorizeActivity();
                }
            }
        }, 1000);

    }


    private void initDefaultPackSetting() {
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<ProductBase> productBaseList = productBaseService.queryAllProductBase();
                if (productBaseList==null || productBaseList.size()==0){
                    long productBaseId= productBaseService.insert(new ProductBase(null,"001","默认"));
                    SharedPreferences.Editor editor=getSharedPreferences(Constants.PackPreference.PACK_SETTING,MODE_PRIVATE).edit();
                    editor.putLong(Constants.PackPreference.PRODUCT_ID,productBaseId);
                    editor.apply();
                }
                List<Staff> staffList=staffService.queryAllStaff();
                if (staffList==null || staffList.size()==0){
                    long staffId= staffService.insert(new Staff(null,"001","默认"));
                    SharedPreferences.Editor editor=getSharedPreferences(Constants.PackPreference.PACK_SETTING,MODE_PRIVATE).edit();
                    editor.putLong(Constants.PackPreference.STAFF_ID,staffId);
                    editor.apply();
                }
            }
        });
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
        Intent intent=new Intent(WelcomeActivity.this, AuthorizeActivityImpl.class);
        startActivity(intent);
        finish();
    }
    private void gotoMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, PackMainActivity.class);
        startActivity(intent);
        finish();
    }

}
