package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunsu.common.activity.BaseActivity;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackInfoEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
import com.yunsu.receiver.BarcodeReceiver;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.ProductService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.sqlite.service.impl.ProductServiceImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PackScanActivity extends BaseActivity {

    @ViewById(id = R.id.et_get_product_key)
    protected EditText et_get_product_key;

    @ViewById(id = R.id.tv_pack_count)
    protected TextView tv_pack_count;

    @ViewById(id = R.id.tv_scan_key)
    protected TextView tv_scan_key;

    @ViewById(id = R.id.tv_standard)
    protected TextView tv_standard;

    @ViewById(id = R.id.progressBar1)
    protected ProgressBar progressBar;

    @ViewById(id = R.id.tv_product)
    protected TextView tv_product;

    @ViewById(id = R.id.title_bar)
    protected TitleBar titleBar;

    @ViewById(id = R.id.tv_staff)
    protected TextView tv_staff;

    @ViewById(id = R.id.tv_standard_in_progress)
    protected TextView tv_standard_in_progress;

    protected TextView tv_product_count;

    protected Button btn_confirm_pack;

    protected Button btn_revoke;

    protected SoundPool soundPool;
    protected HashMap<Integer, Integer> soundMap;

    protected AlertDialog packAlertDialog;

    protected PackInfoEntity packInfoEntity;

    protected static final int PACK_SUCCESS_MSG = 100;

    protected static final int MSG_PACK_KEY_HAS_USED = -12;

    public static final int REVOKE_PACK_REQUEST = 201;

    private String formatPackKey;

    private TextView tv_show_pack_key;

    protected int packCount = 0;
    protected List<String> productKeyList;

    private SimpleDateFormat format;

    private String keyType;
    private EditText et_pack_key;

    private ScanBarcodeReceiver mReceiver=new ScanBarcodeReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        findId();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BARCODE_SERVICE_BROADCAST);
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private class ScanBarcodeReceiver extends BarcodeReceiver {

        @Override
        protected void doWithReceiver(Intent intent) {
            String string = intent.getStringExtra(Constants.KEY_BARCODE_STR);
            if (keyType.equals(Constants.PRODUCT_KEY)){
                dealWithProductKey(string);
            }else if (keyType.equals(Constants.PACK_KEY)){
                dealWithPackKey(string);
            }
        }
    }

    private void findId() {
        titleBar = (TitleBar) findViewById(R.id.title_bar);
        et_get_product_key = (EditText) findViewById(R.id.et_get_product_key);
        tv_pack_count = (TextView) findViewById(R.id.tv_pack_count);
        tv_scan_key = (TextView) findViewById(R.id.tv_scan_key);
        tv_standard = (TextView) findViewById(R.id.tv_standard);
        tv_scan_key = (TextView) findViewById(R.id.tv_scan_key);
        tv_staff = (TextView) findViewById(R.id.tv_staff);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_standard_in_progress = (TextView) findViewById(R.id.tv_standard_in_progress);
        tv_scan_key = (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key = (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key = (TextView) findViewById(R.id.tv_scan_key);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        tv_product_count = (TextView) findViewById(R.id.tv_product_count);
        btn_confirm_pack = (Button) findViewById(R.id.btn_confirm_pack);
        btn_revoke = (Button) findViewById(R.id.btn_revoke);
    }

    private void init() {

        keyType=Constants.PRODUCT_KEY;

        productKeyList = new ArrayList<String>();
        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.short_sound, 1));
        soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.long_sound, 1));
        soundMap.put(3,soundPool.load(getApplicationContext(),R.raw.pack_complete,1));
        soundMap.put(4,soundPool.load(getApplicationContext(),R.raw.prod_invalid,1));
        soundMap.put(5,soundPool.load(getApplicationContext(),R.raw.pack_invalid,1));
        soundMap.put(6,soundPool.load(getApplicationContext(),R.raw.pack_packed,1));
        soundMap.put(7,soundPool.load(getApplicationContext(),R.raw.prod_exist,1));

        format = new SimpleDateFormat(Constants.dateFormat);

        packInfoEntity = getIntent().getParcelableExtra(PackSettingActivity.PACK_INFO);
        tv_staff.setText(packInfoEntity.getStaffName());
        tv_product.setText(packInfoEntity.getProductBaseName());
        tv_standard.setText(String.valueOf(packInfoEntity.getStandard()));
        progressBar.setMax(packInfoEntity.getStandard());
        tv_standard_in_progress.setText(String.valueOf(packInfoEntity.getStandard()));

        getActionBar().hide();
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.pack_scan));

        refreshUI();

        bindProductKeyChanged();

        bindButtonClicked();
    }


    private void refreshUI() {
        progressBar.setProgress(productKeyList.size());
        tv_product_count.setText(String.valueOf(productKeyList.size()));
        tv_pack_count.setText(String.valueOf(packCount));
        if (productKeyList.size() == 0) {
            et_get_product_key.setVisibility(View.VISIBLE);
            et_get_product_key.requestFocus();
            btn_confirm_pack.setEnabled(false);
            btn_revoke.setEnabled(false);
        } else if (productKeyList.size() > 0 && productKeyList.size() < packInfoEntity.getStandard()) {
            et_get_product_key.setVisibility(View.VISIBLE);
            btn_confirm_pack.setEnabled(true);
            btn_revoke.setEnabled(true);
        } else {
            et_get_product_key.setVisibility(View.GONE);
            btn_confirm_pack.setEnabled(true);
            btn_revoke.setEnabled(true);
        }
    }

    private void bindProductKeyChanged() {
        et_get_product_key.requestFocus();
        et_get_product_key.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = et_get_product_key.getText().toString();
                if(StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                dealWithProductKey(string);
            }
        });
    }

    private void dealWithProductKey(String string){
        try {
            String formalizeKey=YunsuKeyUtil.getInstance().verifyProductKey(string);
            if (productKeyList != null && productKeyList.size() > 0) {
                if (productKeyList.contains(formalizeKey)) {
                    soundPool.play(soundMap.get(7), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showErrorMessage(PackScanActivity.this, R.string.product_key_exist_in_this_pack, true);
                    return;
                }
            }
            tv_scan_key.setText(formalizeKey);
            productKeyList.add(formalizeKey);
            refreshUI();
            playSound();
            if (productKeyList.size()==packInfoEntity.getStandard()){
                showPackDialog();
            }

        } catch (NotVerifyException e) {
            soundPool.play(soundMap.get(4), 1, 1, 0, 0, 1);
            ToastMessageHelper.showErrorMessage(getApplicationContext(), e.getMessage(), true);
        }catch (Exception e){
            ToastMessageHelper.showErrorMessage(getApplicationContext(), e.getMessage(), true);
        }finally {
            et_get_product_key.setText("");
        }
    }

    private void playSound() {
        if (productKeyList.size() != packInfoEntity.getStandard()) {
            soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
        } else {
            soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1);
        }
    }

    private void bindButtonClicked() {
        btn_confirm_pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPackDialog();
            }
        });

        btn_revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeProduct();
            }
        });
    }

    private void revokeProduct(){
        Intent intent = new Intent(PackScanActivity.this, RevokeActivity.class);
        intent.putExtra(Constants.PACK_INFO, packInfoEntity);
        intent.putExtra(Constants.PRODUCT_KEY_LIST, (Serializable) productKeyList);
        startActivityForResult(intent, REVOKE_PACK_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVOKE_PACK_REQUEST
                && resultCode == RevokeActivity.REVOKE_PACK_RESULT) {
            try {
                ArrayList<String> list = (ArrayList<String>) data.getSerializableExtra(Constants.PRODUCT_KEY_LIST);
                productKeyList.clear();
                productKeyList.addAll(list);
                refreshUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //弹出扫描包装码弹窗
    private void showPackDialog() {

        keyType=Constants.PACK_KEY;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scan_pack_key, null);
        et_pack_key = (EditText) view.findViewById(R.id.et_pack_key);
        tv_show_pack_key = (TextView) view.findViewById(R.id.tv_show_pack_key);

        et_pack_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = et_pack_key.getText().toString();
                if (StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                dealWithPackKey(string);
            }
        });

        builder.setView(view);
        packAlertDialog = builder.create();
        packAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onBackPressed();
            }
        });
        packAlertDialog.setCanceledOnTouchOutside(false);
        packAlertDialog.show();
    }


    private void dealWithPackKey(String string){
        try {
            formatPackKey = YunsuKeyUtil.getInstance().verifyPackageKey(string);
            showLoading();
            ServiceExecutor.getInstance().execute(new Runnable() {

                @Override
                public void run() {

                    PackService packService = new PackServiceImpl();
                    ProductService productService=new ProductServiceImpl();
                    Pack pack = new Pack();
                    pack.setPackKey(formatPackKey);
                    pack.setLastSaveTime(format.format(new Date()));
                    pack.setStatus(Constants.DB.NOT_SYNC);
                    pack.setProductBaseId(packInfoEntity.getProductBaseId());
                    pack.setStaffId(packInfoEntity.getStaffId());
                    pack.setStandard(packInfoEntity.getStandard());
                    pack.setRealCount(productKeyList.size());
                    try {
                        long packId=packService.addPack(pack);
                        List<Product> productList=new ArrayList<Product>();
                        for(String string : productKeyList){
                            Product product=new Product();
                            product.setProductKey(string);
                            product.setPackId(packId);
                            productList.add(product);
                        }
                        productService.addProductsInTx(productList);
                        Message message = Message.obtain();
                        message.what = PACK_SUCCESS_MSG;
                        mHandler.sendMessage(message);

                    } catch (Exception e) {
                        Message message = Message.obtain();
                        message.what = MSG_PACK_KEY_HAS_USED;
                        mHandler.sendMessage(message);
                        e.printStackTrace();
                    }
                }
            });

        } catch (NotVerifyException e) {
            soundPool.play(soundMap.get(5), 1, 1, 0, 0, 1);
            ToastMessageHelper.showMessage(PackScanActivity.this, e.getMessage(), true);
        }catch (Exception e) {
            ToastMessageHelper.showMessage(PackScanActivity.this, e.getMessage(), true);
        } finally {
            et_pack_key.setText("");
        }
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PACK_SUCCESS_MSG:
                    hideLoading();
                    keyType=Constants.PRODUCT_KEY;
                    tv_show_pack_key.setText(formatPackKey);
                    packAlertDialog.dismiss();
                    doAfterPack();
                    break;
                case MSG_PACK_KEY_HAS_USED:
                    hideLoading();
                    soundPool.play(soundMap.get(6), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showMessage(PackScanActivity.this,R.string.pack_key_has_been_used,false);
                    break;
            }
        }
    };


    private void doAfterPack() {
        soundPool.play(soundMap.get(3), 1, 1, 0, 0, 1);
        ToastMessageHelper.showMessage(PackScanActivity.this, R.string.pack_finish, true);
        packCount++;
        productKeyList.clear();
        refreshUI();
    }

}
