package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackInfoEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RevokeActivity extends BaseActivity {

    @ViewById(id = R.id.revoke_title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.tv_staff)
    private TextView tv_staff;

    @ViewById(id = R.id.tv_product)
    private TextView tv_product;

    @ViewById(id = R.id.tv_standard)
    private TextView tv_standard;

    @ViewById(id = R.id.progressBar1)
    private ProgressBar progressBar;

    @ViewById(id = R.id.tv_standard_in_progress)
    private TextView tv_standard_in_progress;

    @ViewById(id = R.id.tv_product_count)
    private TextView tv_product_count;

    @ViewById(id = R.id.et_get_pack_key)
    private EditText et_get_product_key;

    @ViewById(id = R.id.tv_scan_key)
    private TextView tv_scan_key;

    @ViewById(id = R.id.tc_scan_error_tip)
    private TextView tc_scan_error_tip;

    @ViewById(id = R.id.ll_scan_key)
    private LinearLayout ll_scan_key;

    SoundPool soundPool;
    HashMap<Integer, Integer> soundMap;

    private PackInfoEntity packInfoEntity;

    private List<String> productKeyList;

    public static final int REVOKE_PACK_RESULT=389;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revoke);
        init();
    }

    private void init() {

        getActionBar().hide();
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.remove_product));

        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.short_sound, 1));
        soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.long_sound, 1));

        packInfoEntity = getIntent().getParcelableExtra(Constants.PACK_INFO);
        tv_staff.setText(packInfoEntity.getStaffName());
        tv_product.setText(packInfoEntity.getProductBaseName());
        tv_standard.setText(String.valueOf(packInfoEntity.getStandard()));
        progressBar.setMax(packInfoEntity.getStandard());
        tv_standard_in_progress.setText(String.valueOf(packInfoEntity.getStandard()));

        productKeyList=new ArrayList<>();

        try {
            List<String> list= (ArrayList<String>) getIntent().getSerializableExtra(Constants.PRODUCT_KEY_LIST);
            productKeyList.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshUI();

        bindProductKeyChanged();
    }

    private void refreshUI() {
        progressBar.setProgress(productKeyList.size());
        tv_product_count.setText(String.valueOf(productKeyList.size()));
        if (productKeyList.size()==0){
            AlertDialog dialog = new AlertDialog.Builder(RevokeActivity.this).setMessage(R.string.has_no_one_in_pack)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent();
                            intent.putExtra(Constants.PRODUCT_KEY_LIST, (Serializable) productKeyList);
                            setResult(REVOKE_PACK_RESULT,intent);
                            finish();
                        }
                    }).setNegativeButton(R.string.no, null).create();
            dialog.setCancelable(false);
            dialog.show();
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
                if (StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                try {
                    String formalizeKey=YunsuKeyUtil.getInstance().verifyProductKey(string);
                    if (productKeyList.contains(formalizeKey)){
                        productKeyList.remove(formalizeKey);
                        ll_scan_key.setVisibility(View.VISIBLE);
                        tc_scan_error_tip.setVisibility(View.INVISIBLE);
                    }else {
                        ToastMessageHelper.showErrorMessage(RevokeActivity.this,R.string.product_not_in_pack,true);
                        ll_scan_key.setVisibility(View.INVISIBLE);
                        tc_scan_error_tip.setVisibility(View.VISIBLE);
                        tc_scan_error_tip.setText(R.string.product_not_in_pack);
                    }
                    tv_scan_key.setText(formalizeKey);
                    refreshUI();
                    playSound();

                } catch (Exception e) {
                    ToastMessageHelper.showErrorMessage(getApplicationContext(), e.getMessage(), true);
                } finally {
                    et_get_product_key.setText("");
                }

            }
        });
    }

    private void playSound() {
        if (productKeyList.size() >0) {
            soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
        } else {
            soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra(Constants.PRODUCT_KEY_LIST, (Serializable) productKeyList);
        setResult(REVOKE_PACK_RESULT,intent);
        super.onBackPressed();
    }
}
