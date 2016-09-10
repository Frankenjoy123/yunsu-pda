package com.yunsu.activity;

import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.view.TitleBar;
import com.yunsu.unity.ListItem;

import java.util.HashMap;
import java.util.List;

public class PackScanActivity extends BaseActivity {

    private static final int MSG_SUCCESS = 1;
    private static final int MSG_FAILURE = -1;

    private int standard = 5;
    private int count = 0;
    private int finishedBags = 0;
    List<ListItem> listItems;

    @ViewById(id = R.id.et_get_product_key)
    private EditText et_get_product_key;

    @ViewById(id = R.id.tv_pack_count)
    private TextView tv_pack_count;

    @ViewById(id = R.id.tv_scan_key)
    private TextView tv_scan_key;

    @ViewById(id = R.id.tv_standard_value)
    private TextView input;

    private ImageView downArrow;

    @ViewById(id = R.id.progressBar1)
    private ProgressBar progressBar;

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.tv_staff)
    private TextView tv_staff;

    @ViewById(id = R.id.tv_standard)
    private TextView tv_standard;

    @ViewById(id = R.id.tv_product)
    private TextView tv_product;

    @ViewById(id = R.id.tv_product_count)
    private TextView tv_product_count;

    @ViewById(id=R.id.tv_standard_in_progress)
    private TextView tv_standard_in_progress;

    @ViewById(id = R.id.btn_confirm_pack)
    private Button btn_confirm_pack;

    @ViewById(id=R.id.btn_revoke)
    private Button btn_revoke;

    SoundPool soundPool;

    HashMap<Integer, Integer> soundMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_scan);
        init();
    }

    private void init() {

        et_get_product_key= (EditText) findViewById(R.id.et_get_product_key);
        tv_pack_count= (TextView) findViewById(R.id.tv_pack_count);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);
        tv_scan_key= (TextView) findViewById(R.id.tv_scan_key);


        /**
         *

         @ViewById(id = R.id.tv_standard_value)
         private TextView input;

         private ImageView downArrow;

         @ViewById(id = R.id.progressBar1)
         private ProgressBar progressBar;

         @ViewById(id = R.id.title_bar)
         private TitleBar titleBar;

         @ViewById(id = R.id.tv_note_scan)
         private TextView tv_note_scan_pack;

         @ViewById(id = R.id.tv_staff)
         private TextView tv_staff;

         @ViewById(id = R.id.tv_standard)
         private TextView tv_standard;

         @ViewById(id = R.id.tv_product)
         private TextView tv_product;

         @ViewById(id = R.id.tv_product_count)
         private TextView tv_product_count;

         @ViewById(id=R.id.tv_standard_in_progress)
         private TextView tv_standard_in_progress;

         @ViewById(id = R.id.btn_confirm_pack)
         private Button btn_confirm_pack;

         @ViewById(id=R.id.btn_revoke)
         private Button btn_revoke;
         */

        titleBar= (TitleBar) findViewById(R.id.title_bar);

        getActionBar().hide();
        titleBar.setTitle(getString(R.string.pack_scan));
//        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
//        titleBar.setDisplayAsBack(true);

//        productKeyList = new ArrayList<ListItem>();
//
//        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
//        soundMap = new HashMap<Integer, Integer>();
//        soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.short_sound, 1));
//        soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.long_sound, 1));

    }



}
