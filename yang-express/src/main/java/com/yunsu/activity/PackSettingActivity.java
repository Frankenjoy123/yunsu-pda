package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import java.lang.reflect.Field;

public class PackSettingActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.rl_choose_standard)
    private RelativeLayout rl_choose_standard;

    @ViewById(id = R.id.rl_choose_staff)
    private RelativeLayout rl_choose_staff;

    @ViewById(id = R.id.rl_choose_product)
    private RelativeLayout rl_choose_product;

    @ViewById(id = R.id.tv_staff)
    private TextView tv_staff;

    @ViewById(id = R.id.tv_product)
    private TextView tv_product;

    @ViewById(id = R.id.btn_start_pack)
    private Button btn_start_pack;

    @ViewById(id = R.id.tv_standard_value)
    private TextView tv_standard_value;

    private EditText et_pack_standard;

    public static final int STAFF_REQUEST = 123;

    public static final int STAFF_RESULT = 145;

    private static final int QUERY_STAFF_MSG = 156;

    private static final int RESTORE_SETTING_MSG=168;

    public static final String STAFF_ID = "staff_id";

    public static final String PRODUCT_BASE_ID = "product_base_id";

    public static final int PRODUCT_BASE_REQUEST = 212;

    public static final int PRODUCT_BASE_RESULT = 245;

    private static final int QUERY_PRODUCT_BASE_MSG = 256;

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
        setContentView(R.layout.activity_create_pack);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.pack_setting));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

        staffService = new StaffServiceImpl();
        productBaseService = new ProductBaseServiceImpl();

        rl_choose_standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(standard);
            }
        });

        rl_choose_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent staffIntent = new Intent(PackSettingActivity.this, StaffListActivity.class);
                if (staff!=null){
                    staffIntent.putExtra(STAFF_ID,staff.getId());
                }
                startActivityForResult(staffIntent, STAFF_REQUEST);
            }
        });

        rl_choose_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productIntent = new Intent(PackSettingActivity.this, ProductBaseListActivity.class);
                if (productBase!=null){
                    productIntent.putExtra(PRODUCT_BASE_ID,productBase.getId());
                }
                startActivityForResult(productIntent, PRODUCT_BASE_REQUEST);
            }
        });

        btn_start_pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String standardText = tv_standard_value.getText().toString();
                String staffText = tv_staff.getText().toString();
                String productText = tv_product.getText().toString();
                if (StringHelper.isStringNullOrEmpty(standardText)) {
                    ToastMessageHelper.showErrorMessage(PackSettingActivity.this, R.string.set_pack_standard, true);
                } else if (StringHelper.isStringNullOrEmpty(staffText)) {
                    ToastMessageHelper.showErrorMessage(PackSettingActivity.this, R.string.set_staff, true);
                } else if (StringHelper.isStringNullOrEmpty(productText)) {
                    ToastMessageHelper.showErrorMessage(PackSettingActivity.this, R.string.set_product, true);
                } else {

                    Intent intent = new Intent(PackSettingActivity.this, PackScanActivity.class);
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
                }
            }
        });

        restoreSetting();

    }



    private void dialog(int standardNum){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_standard);
        LayoutInflater inflater=getLayoutInflater();
        final View view=inflater.inflate(R.layout.dialog_pack_standard,null);
        et_pack_standard= (EditText) view.findViewById(R.id.et_pack_standard);
        et_pack_standard.setText(String.valueOf(standardNum));
        et_pack_standard.setSelection(et_pack_standard.getText().length());
        builder.setView(view);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean closeDialog;
                String numString=et_pack_standard.getText().toString();
                if (StringHelper.isStringNullOrEmpty(numString)){
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(PackSettingActivity.this,"规格不能为空",true);
                } else if (numString.startsWith("0")){
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(PackSettingActivity.this,"请输入合法的数字",true);
                }else if (numString.length()<=4&&(Integer.parseInt(numString)<=1000)){
                    closeDialog=true;
                    tv_standard_value.setText(String.valueOf(numString));
                    standard=Integer.parseInt(numString);
//                    InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    inputMethodManager.hideSoftInputFromInputMethod(et_pack_standard.getWindowToken(),0);
                }else {
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(PackSettingActivity.this,"请输入1000以内的数字",true);
                }
                try {
                    //下面三句控制弹框的关闭
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface,closeDialog);//true表示要关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //下面三句控制弹框的关闭
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface,true);//true表示要关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog=builder.create();
        dialog.show();


//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                if(et_pack_standard!=null){
//                    //设置可获得焦点
//                    et_pack_standard.setFocusable(true);
//                    et_pack_standard.setFocusableInTouchMode(true);
//                    //请求获得焦点
//                    et_pack_standard.requestFocus();
//                    //调用系统输入法
//                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputManager.showSoftInput(et_pack_standard,InputMethodManager.SHOW_FORCED );
//                }
//
//            }
//        }, 200);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case STAFF_RESULT:
                showLoading();
                final long staffId = data.getLongExtra(STAFF_ID, 0);
                ServiceExecutor.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        staff = staffService.queryStaffById(staffId);
                        Message message = Message.obtain();
                        message.what = QUERY_STAFF_MSG;
                        message.obj = staff;
                        handler.sendMessage(message);
                    }
                });
                break;

            case PRODUCT_BASE_RESULT:
                showLoading();
                final long productId = data.getLongExtra(Constants.PRODUCT_BASE_ID, 0);
                ServiceExecutor.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        productBase = productBaseService.queryProductBaseById(productId);
                        Message message = Message.obtain();
                        message.what = QUERY_PRODUCT_BASE_MSG;
                        message.obj = productBase;
                        handler.sendMessage(message);
                    }
                });
                break;

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_STAFF_MSG:
                    hideLoading();
                    if (msg.obj==null){
                        tv_staff.setText("");
                    }else {
                        tv_staff.setText(((Staff) msg.obj).getName());
                    }
                    break;

                case QUERY_PRODUCT_BASE_MSG:
                    hideLoading();
                    if (msg.obj==null){
                        tv_product.setText("");
                    }else {
                        tv_product.setText(((ProductBase) msg.obj).getName());
                    }
                    break;

                case RESTORE_SETTING_MSG:
                    hideLoading();
                    refreshUI();
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


    @Override
    protected void onPause() {
        super.onPause();
        saveSetting();
    }

    private void saveSetting(){
        SharedPreferences.Editor editor=getSharedPreferences(Constants.PackPreference.PACK_SETTING,MODE_PRIVATE).edit();
        if (staff!=null){
            editor.putLong(Constants.PackPreference.STAFF_ID,staff.getId());
        }
        if (productBase!=null){
            editor.putLong(Constants.PackPreference.PRODUCT_ID,productBase.getId());
        }
        editor.putInt(Constants.PackPreference.STANDARD,standard);
        editor.apply();

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
