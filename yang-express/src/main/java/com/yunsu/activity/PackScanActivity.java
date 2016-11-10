package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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

    private EditText et_express_key;


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

//    private Intent intentService = new Intent("com.hyipc.core.service.barcode.BarcodeService2D");
    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String TONE = "TONE=100";
    //send by BarcodeService
    public static final String ACTION_BARCODE_SERVICE_BROADCAST = "action_barcode_broadcast";
    private BroadcastReceiver mReceiver = new BarcodeReceiver();
    //send by the BarcodeService
    public static final String KEY_BARCODE_STR = "key_barcode_string";

    private String keyType;


    // 当按键按下，便会触发一条广播，在此被接收
    public class BarcodeReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(ACTION_BARCODE_SERVICE_BROADCAST)) {
                String strBarcode = intent.getExtras().getString(KEY_BARCODE_STR);
                if (keyType.equals(Constants.PACK_KEY)){
                    dealWithPackKey(strBarcode);
                }else if (keyType.equals(Constants.EXPRESS_KEY)){
                    dealWithExpressKey(strBarcode);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BARCODE_SERVICE_BROADCAST);
        this.registerReceiver(mReceiver, filter);
    }

//    @Override
//    protected void onPause() {
//        //close barcodePower
//        this.unregisterReceiver(mReceiver);
//        super.onPause();
//    }

//    public void doClick(View v){
//        if (v.equals(btnScan)) {
//            intentService.putExtra(KEY_ACTION, "UP");
//            this.startService(intentService);
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            intentService.putExtra(KEY_ACTION, "DOWN");
//            this.startService(intentService);
//        }
//    }


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

        keyType=Constants.PACK_KEY;

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
                dealWithPackKey(string);
            }
        });
    }

    private void dealWithPackKey(String string){
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



    private void showExpressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scan_pack_key, null);
        et_express_key = (EditText) view.findViewById(R.id.et_pack_key);
        tv_show_express_key = (TextView) view.findViewById(R.id.tv_show_pack_key);

        et_express_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = et_express_key.getText().toString();
                if (StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                dealWithExpressKey(string);
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


    private void dealWithExpressKey(String string){
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
            et_express_key.setText("");
        }

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PACK_SUCCESS_MSG:
                    hideLoading();
                    soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1);
                    packAlertDialog.dismiss();
                    doAfterPack();
                    keyType=Constants.PACK_KEY;
                    break;

                case PACK_KEY_EXIST_MSG:
                    soundPool.play(soundMap.get(4), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showMessage(PackScanActivity.this,R.string.pack_key_has_exist,true);
                    break;

                case SCAN_EXPRESS_MSG:
                    soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
                    showExpressDialog();
                    keyType=Constants.EXPRESS_KEY;
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
