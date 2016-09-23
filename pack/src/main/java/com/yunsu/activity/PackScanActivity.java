package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackInfoEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.manager.FileManager;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

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
    protected static final int MSG_FAILURE = -1;

    protected static final int MSG_PACK_KEY_HAS_USED = -12;

    public static final int REVOKE_PACK_REQUEST = 201;


    protected int packCount = 0;
    protected List<String> productKeyList;

    private SimpleDateFormat format;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        findId();
        init();
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

        productKeyList = new ArrayList<String>();
        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.short_sound, 1));
        soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.long_sound, 1));

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
            btn_confirm_pack.setEnabled(false);
            btn_revoke.setEnabled(false);
        } else if (productKeyList.size() > 0 && productKeyList.size() < packInfoEntity.getStandard()) {
            btn_confirm_pack.setEnabled(true);
            btn_revoke.setEnabled(true);
        } else {
            showPackDialog();
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
                String string = new StringBuilder(s).toString();
                try {
                    String formalizeKey = YunsuKeyUtil.verifyScanKey(string);
                    if (productKeyList != null && productKeyList.size() > 0) {
                        if (productKeyList.contains(formalizeKey)) {
                            ToastMessageHelper.showErrorMessage(PackScanActivity.this, R.string.product_key_exist_in_this_pack, true);
                            return;
                        }
                    }
                    tv_scan_key.setText(formalizeKey);
                    productKeyList.add(formalizeKey);
                    refreshUI();
                    playSound();

                } catch (Exception e) {
                    ToastMessageHelper.showErrorMessage(getApplicationContext(), e.getMessage(), true);
                }

            }
        });
    }

    private void playSound() {
        if (productKeyList.size() != packInfoEntity.getStandard()) {
            soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
        } else {
            soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1);
            et_get_product_key.clearFocus();
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
                Intent intent = new Intent(PackScanActivity.this, RevokeActivity.class);
                intent.putExtra(Constants.PACK_INFO, packInfoEntity);
                intent.putExtra(Constants.PRODUCT_KEY_LIST, (Serializable) productKeyList);
                startActivityForResult(intent, REVOKE_PACK_REQUEST);
            }
        });
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

    private void showPackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scan_pack_key, null);
        final EditText et_pack_key = (EditText) view.findViewById(R.id.et_pack_key);
        final TextView tv_show_pack_key = (TextView) view.findViewById(R.id.tv_show_pack_key);
        et_pack_key.requestFocus();

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
                try {
                    final String formatKey = YunsuKeyUtil.verifyPackKey(string);
                    tv_show_pack_key.setText(formatKey);
                    showLoading();
                    ServiceExecutor.getInstance().execute(new Runnable() {

                        @Override
                        public void run() {

                            PackService packService = new PackServiceImpl();
                            Pack pack = new Pack();
                            pack.setPackKey(formatKey);
                            pack.setLastSaveTime(format.format(new Date()));
                            pack.setStatus(Constants.DB.NOT_SYNC);
                            pack.setProductBaseId(packInfoEntity.getProductBaseId());
                            pack.setStaffId(packInfoEntity.getStaffId());
                            pack.setStandard(packInfoEntity.getStandard());
                            pack.setRealCount(productKeyList.size());
                            try {
                                packService.addPack(pack);
                                FileManager.getInstance().writePackInfoToFile(pack.getPackKey(),productKeyList);
                                Message message = Message.obtain();
                                message.what = PACK_SUCCESS_MSG;
                                mHandler.sendMessage(message);

                            } catch (Exception e) {
                                Message message = Message.obtain();
                                message.what = MSG_PACK_KEY_HAS_USED;
                                mHandler.sendMessage(message);
                                e.printStackTrace();
                            }
//                            ProductService productService = new ProductServiceImpl();
//                            for (int i = 0; i < productKeyList.size(); i++) {
//                                Product product = new Product();
//                                product.setPackId(pack.getId());
//                                product.setProductKey(productKeyList.get(i));
//                                productService.addProduct(product);
//                            }


                        }
                    });

                } catch (Exception e) {
                    ToastMessageHelper.showErrorMessage(PackScanActivity.this, e.getMessage(), true);
                }
//                } catch (Exception e) {
//                    Message message = Message.obtain();
//                    message.what = MSG_FAILURE;
//                    mHandler.sendMessage(message);
//                    e.printStackTrace();
//                }
            }
        });

        builder.setView(view);
        builder.setCancelable(false);
        packAlertDialog = builder.create();
        packAlertDialog.show();
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PACK_SUCCESS_MSG:
                    hideLoading();
                    packAlertDialog.dismiss();
                    doAfterPack();
                    break;
                case MSG_FAILURE:
                    hideLoading();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "打包失败，请检查", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                case MSG_PACK_KEY_HAS_USED:
                    hideLoading();
                    ToastMessageHelper.showErrorMessage(PackScanActivity.this,R.string.pack_key_has_been_used,false);
                    break;
            }
        }
    };


    private void doAfterPack() {
        ToastMessageHelper.showMessage(PackScanActivity.this, R.string.pack_finish, true);
        packCount++;
        productKeyList.clear();
        et_get_product_key.requestFocus();

        refreshUI();
    }

}
