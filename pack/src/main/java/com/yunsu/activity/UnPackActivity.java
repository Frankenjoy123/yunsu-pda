package com.yunsu.activity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.manager.FileManager;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.ProductService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.sqlite.service.impl.ProductServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UnPackActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_get_pack_key)
    private EditText et_get_pack_key;

    private Pack queryResultPack;

    private ProductService productService;
    private PackService packService;

    private static final int QUERY_PACK_SUCCESS_MSG =145;

    private static final int QUERY_PACK_FAIL_MSG=167;

    protected SoundPool soundPool;

    protected HashMap<Integer, Integer> soundMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_pack);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.unpack_package));

        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.pack_discard, 1));
        soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.pack_invalid, 1));
        soundMap.put(3, soundPool.load(getApplicationContext(), R.raw.pack_not_exist, 1));
        packService=new PackServiceImpl();
        productService=new ProductServiceImpl();

        bindPackKeyChanged();
    }

    private void bindPackKeyChanged() {
        et_get_pack_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String string = new StringBuilder(editable).toString();
                try {
                    final String formatKey= YunsuKeyUtil.getInstance().verifyPackageKey(string);
                    showLoading();
                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            queryResultPack = packService.QueryPack(formatKey);

                            if (queryResultPack!=null){
                                packService.removePack(queryResultPack);

                                SimpleDateFormat format=new SimpleDateFormat(Constants.dateFormat);
                                SimpleDateFormat format1=new SimpleDateFormat(Constants.dateOnlyDayFormat);
                                try {
                                    Date  date=format.parse( queryResultPack.getLastSaveTime());
                                    String fileName=format1.format(date)+".txt";
                                    FileManager.getInstance().deleteRowInPackFile(fileName,formatKey);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(QUERY_PACK_SUCCESS_MSG);
                            }else {
                                handler.sendEmptyMessage(QUERY_PACK_FAIL_MSG);
                            }
                        }
                    });

                } catch (NotVerifyException e) {
                    soundPool.play(soundMap.get(2), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showErrorMessage(UnPackActivity.this,e.getMessage(),true);
                } catch (Exception e) {
                    ToastMessageHelper.showErrorMessage(UnPackActivity.this,e.getMessage(),true);
                }
            }
        });
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_PACK_SUCCESS_MSG:
                    hideLoading();
                    soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showMessage(UnPackActivity.this,"移除包装"+queryResultPack.getPackKey()+"成功",true);
                    break;

                case QUERY_PACK_FAIL_MSG:
                    hideLoading();
                    soundPool.play(soundMap.get(3), 1, 1, 0, 0, 1);
                    ToastMessageHelper.showErrorMessage(UnPackActivity.this,R.string.not_find_pack,true);
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
