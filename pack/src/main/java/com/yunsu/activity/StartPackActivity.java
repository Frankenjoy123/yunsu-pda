package com.yunsu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackInfoEntity;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.sqlite.service.ProductBaseService;
import com.yunsu.sqlite.service.StaffService;
import com.yunsu.sqlite.service.impl.ProductBaseServiceImpl;
import com.yunsu.sqlite.service.impl.StaffServiceImpl;

public class StartPackActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.tv_staff)
    private TextView tv_staff;

    @ViewById(id = R.id.tv_product)
    private TextView tv_product;

    @ViewById(id = R.id.btn_save)
    private Button btn_start_pack;

    @ViewById(id = R.id.tv_standard_value)
    private TextView tv_standard_value;

    private static final int RESTORE_SETTING_MSG=168;

    public static final String PACK_INFO = "pack_info";

    private static final long FALSE_SETTING= -1;

    private StaffService staffService;

    private ProductBaseService productBaseService;

    private Staff staff;

    private ProductBase productBase;

    private int standard=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_pack);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.start_pack));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        staffService = new StaffServiceImpl();
        productBaseService = new ProductBaseServiceImpl();


        btn_start_pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String standardText = tv_standard_value.getText().toString();
                String staffText = tv_staff.getText().toString();
                String productText = tv_product.getText().toString();
                if (StringHelper.isStringNullOrEmpty(standardText)) {
                    ToastMessageHelper.showErrorMessage(StartPackActivity.this, R.string.set_pack_standard, true);
                } else if (StringHelper.isStringNullOrEmpty(staffText)) {
                    ToastMessageHelper.showErrorMessage(StartPackActivity.this, R.string.set_staff, true);
                } else if (StringHelper.isStringNullOrEmpty(productText)) {
                    ToastMessageHelper.showErrorMessage(StartPackActivity.this, R.string.set_product, true);
                } else {

                    Intent intent = new Intent(StartPackActivity.this, PackScanActivity.class);
                    PackInfoEntity packInfoEntity = new PackInfoEntity();
                    packInfoEntity.setProductBaseId(productBase.getId());
                    packInfoEntity.setProductBaseName(productBase.getName());
                    packInfoEntity.setProductBaseNumber(productBase.getProductNumber());
                    packInfoEntity.setStaffId(staff.getId());
                    packInfoEntity.setStaffName(staff.getName());
                    packInfoEntity.setStaffNumber(staff.getStaffNumber());

                        standard=Integer.valueOf(standardText);
                        packInfoEntity.setStandard(standard);

                    intent.putExtra(PACK_INFO, packInfoEntity);
                    startActivity(intent);
                    finish();
                }
            }
        });

        restoreSetting();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTORE_SETTING_MSG:
                    hideLoading();
                    refreshUI();
                    break;
            }

        }


    };

    private void refreshUI() {
        if (staff!=null){
            tv_staff.setText(staff.getName());
        }
        if (productBase!=null){
            tv_product.setText(productBase.getName());
        }
        tv_standard_value.setText(String.valueOf(standard));
    }

    private void restoreSetting(){
        showLoading();
        SharedPreferences preferences=getSharedPreferences(Constants.PackPreference.PACK_SETTING,MODE_PRIVATE);
        final long tempStaffId=preferences.getLong(Constants.PackPreference.STAFF_ID,FALSE_SETTING);
        final int tempStandard=preferences.getInt(Constants.PackPreference.STANDARD, 32);
        final long tempProductId=preferences.getLong(Constants.PackPreference.PRODUCT_ID,FALSE_SETTING);
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (tempStaffId!=FALSE_SETTING){
                    staff=staffService.queryStaffById(tempStaffId);
                }
                if (tempProductId!=FALSE_SETTING){
                    productBase=productBaseService.queryProductBaseById(tempProductId);
                }
                if (tempStandard!=FALSE_SETTING){
                    standard=tempStandard;
                }
                handler.sendEmptyMessage(RESTORE_SETTING_MSG);
            }
        });

    }
}
