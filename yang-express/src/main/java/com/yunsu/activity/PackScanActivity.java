package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Express;
import com.yunsu.manager.FileManager;
import com.yunsu.sqlite.service.ExpressService;
import com.yunsu.sqlite.service.impl.ExpressServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PackScanActivity extends BaseActivity {

    @ViewById(id = R.id.et_get_pack_key)
    protected EditText et_get_pack_key;

    @ViewById(id = R.id.tv_pair_count)
    protected TextView tv_pair_count;

    @ViewById(id = R.id.tv_scan_key)
    protected TextView tv_scan_key;


    @ViewById(id = R.id.title_bar)
    protected TitleBar titleBar;


    @ViewById(id = R.id.tv_standard_in_progress)

    protected AlertDialog packAlertDialog;


    protected static final int PACK_SUCCESS_MSG = 100;

    private static final int PACK_KEY_EXIST_MSG=301;

    private static final int EXPRESS_KEY_EXIST_MSG=303;

    private static final int SCAN_EXPRESS_MSG=305;

    private static final int RESTORE_COUNT_MSG=307;


    private String formatExpressKey;

    private String packKey;

    private TextView tv_show_express_key;

    protected long pairCount = 0;

    private SimpleDateFormat format;

    private TextView tv_date;

    private ExpressService expressService;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        findId();
        init();
    }

    private void findId() {
        titleBar = (TitleBar) findViewById(R.id.title_bar);
        et_get_pack_key = (EditText) findViewById(R.id.et_get_pack_key);
        tv_pair_count = (TextView) findViewById(R.id.tv_pair_count);
        tv_scan_key = (TextView) findViewById(R.id.tv_scan_key);
        tv_date= (TextView) findViewById(R.id.tv_date);
    }

    private void init() {


        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.short_sound, 1));
        soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.long_sound, 1));
        soundMap.put(3,soundPool.load(getApplicationContext(),R.raw.package_illegal,1));
        soundMap.put(4,soundPool.load(getApplicationContext(),R.raw.package_duplicated,1));
        soundMap.put(5,soundPool.load(getApplicationContext(),R.raw.express_illegal,1));
        soundMap.put(6,soundPool.load(getApplicationContext(),R.raw.express_duplicated,1));

        expressService= new ExpressServiceImpl();

        format = new SimpleDateFormat(Constants.dateOnlyDayFormat);

        tv_date.setText(format.format(new Date()));

        getActionBar().hide();
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.pack_express_scan));

        refreshUI();

        bindPackKeyChanged();

        restoreCount();

    }

    private void restoreCount() {
        showLoading();
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                pairCount=expressService.queryExpressCount(format.format(new Date()));
                mHandler.sendEmptyMessage(RESTORE_COUNT_MSG);
            }
        });

    }

    private void refreshUI() {

        tv_pair_count.setText(String.valueOf(pairCount));

    }

    private void bindPackKeyChanged() {
        et_get_pack_key.requestFocus();
        et_get_pack_key.addTextChangedListener(new TextWatcher() {

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
                if(StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                try {

                    final String tempPackKey=YunsuKeyUtil.getInstance().verifyPackageKey(string);
                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            Express tempExpress=expressService.QueryExpressByPackKey(tempPackKey);
                            if (tempExpress!=null){
                                mHandler.sendEmptyMessage(PACK_KEY_EXIST_MSG);
                            }else {
                                packKey=tempPackKey;
                                mHandler.sendEmptyMessage(SCAN_EXPRESS_MSG);
                            }
                        }
                    });

                } catch (NotVerifyException e) {
                    ToastMessageHelper.showMessage(getApplicationContext(), e.getMessage(), true);
                    soundPool.play(soundMap.get(3), 1, 1, 0, 0, 1);
                }catch (Exception e){
                    ToastMessageHelper.showErrorMessage(getApplicationContext(), e.getMessage(), true);
                }finally {
                    et_get_pack_key.setText("");
                }

            }
        });
    }



    private void showExpressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scan_pack_key, null);
        final EditText et_pack_key = (EditText) view.findViewById(R.id.et_pack_key);
        tv_show_express_key = (TextView) view.findViewById(R.id.tv_show_pack_key);

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
                try {
                    final String tempFormatExpressKey = YunsuKeyUtil.getInstance().verifyExpressKey(string);
                    ServiceExecutor.getInstance().execute(new Runnable() {

                        @Override
                        public void run() {
                            Express tempExpress=expressService.QueryExpressByExpressKey(tempFormatExpressKey);
                            if (tempExpress!=null){
                                mHandler.sendEmptyMessage(EXPRESS_KEY_EXIST_MSG);
                            }else {
                                Express express=new Express();
                                express.setPackKey(packKey);
                                express.setExpressKey(tempFormatExpressKey);
                                SimpleDateFormat format=new SimpleDateFormat(Constants.dateFormat);
                                express.setCreateTime(format.format(new Date()));
                                expressService.addExpress(express);
                                FileManager.getInstance().writeExpressPackInfoToFile(tempFormatExpressKey,packKey);
                                mHandler.sendEmptyMessage(PACK_SUCCESS_MSG);
                            }
                        }
                    });

                } catch (NotVerifyException e) {
                    soundPool.play(soundMap.get(5), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showMessage(PackScanActivity.this, e.getMessage(), true);
                }finally {
                    et_pack_key.setText("");
                }
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


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PACK_SUCCESS_MSG:
                    hideLoading();
                    soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1);
                    packAlertDialog.dismiss();
                    doAfterPack();
                    break;

                case PACK_KEY_EXIST_MSG:
                    soundPool.play(soundMap.get(4), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showMessage(PackScanActivity.this,R.string.pack_key_has_exist,true);
                    break;

                case SCAN_EXPRESS_MSG:
                    soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
                    showExpressDialog();
                    break;

                case EXPRESS_KEY_EXIST_MSG:
                    soundPool.play(soundMap.get(6), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showMessage(PackScanActivity.this,R.string.express_key_has_exist,true);
                    break;

                case RESTORE_COUNT_MSG:
                    hideLoading();
                    refreshUI();
                    break;

            }
        }
    };


    private void doAfterPack() {
        pairCount++;
        refreshUI();
    }

}
