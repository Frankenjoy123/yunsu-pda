package com.yunsu.activity;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackInfoEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.ProductService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.sqlite.service.impl.ProductServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PackScanActivity extends BaseActivity {

    private static final int PACK_SUCCESS_MSG = 100;
    private static final int MSG_FAILURE = -1;

    private List<Integer> standards = new ArrayList<Integer>();
    private int standard = 5;
    private int productCount = 0;
    private int packCount = 0;
    List<String> productKeyList;

    private PopupWindow popWin;
    private ListView listView;
    private TextView progressText;

    @ViewById(id = R.id.et_get_product_key)
    private EditText et_get_product_key;

    @ViewById(id = R.id.tv_pack_count)
    private TextView tv_pack_count;

    @ViewById(id = R.id.tv_scan_key)
    private TextView tv_scan_key;

    @ViewById(id = R.id.tv_standard)
    private TextView tv_standard;

    private ImageView downArrow;

    @ViewById(id = R.id.progressBar1)
    private ProgressBar progressBar;

    @ViewById(id = R.id.tv_product)
    private TextView tv_product;


    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.tv_staff)
    private TextView tv_staff;

    @ViewById(id = R.id.tv_standard_in_progress)
    private TextView tv_standard_in_progress;

    private TextView tv_product_count;

    private Button btn_confirm_pack;

    private Button btn_revoke;

    SoundPool soundPool;
    HashMap<Integer, Integer> soundMap;

    private AlertDialog packAlertDialog;

    private PackInfoEntity packInfoEntity;


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
        } else if (productKeyList.size() > 0 && productKeyList.size() < standard) {
            btn_confirm_pack.setEnabled(true);
            btn_revoke.setEnabled(true);
        }
        else {
            showPackDialog();
        }
    }


//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("ZXW", "MainActivity onActivityResult");
//        if (requestCode == 0 && resultCode == 0) {
//            boolean isClearAll = data.getBooleanExtra("isClearAll", false);
//            if (isClearAll) {
//                productKeyList.clear();
//                productCount = 0;
//            } else {
//                ArrayList list = data.getParcelableArrayListExtra("delete_titles");
//                ArrayList<String> delete_titles = (ArrayList<String>) list.get(0);
//
//                for (int i = 0; i < delete_titles.size(); i++) {
//                    String string = delete_titles.get(i);
//                    for (int j = 0; j < productKeyList.size(); j++) {
//                        if (productKeyList.get(j).getTitle().equals(delete_titles.get(i))) {
//                            productKeyList.remove(j);
//                            productCount--;
//                        }
//                    }
//                }
//            }
//            if (productCount < standard) {
//                et_get_pack_key.clearFocus();
//                et_get_product_key.requestFocus();
//            }
//            progressBar.setProgress(productCount);
//            progressText.setText("当前进度" + productCount + "/" + standard);
//        }
//
//        if (requestCode == 200 & resultCode == 200) {
//
//            int temp = data.getIntExtra("et_standard", -1);
//            if (!standards.contains(temp) || temp != -1) {
//                standard = temp;
//                standards.add(standard);
//            }
//            if (data.getIntExtra("edit_count", -1) >= 0) {
//                packCount = data.getIntExtra("edit_count", -1);
//            }
//
//            refreshUI();
//
//        }
//    }


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

                } catch (NotVerifyException e) {
                    ToastMessageHelper.showErrorMessage(getApplicationContext(), e.getMessage(), true);
                }

            }
        });
    }

    private void playSound() {
        if (productKeyList.size() != standard) {
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

            }
        });
    }

    private void showPackDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_scan_pack_key,null);
        final EditText et_pack_key= (EditText) view.findViewById(R.id.et_pack_key);
        final TextView tv_show_pack_key= (TextView) view.findViewById(R.id.tv_show_pack_key);
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
                    final String formatKey = YunsuKeyUtil.verifyScanKey(string);
                    tv_show_pack_key.setText(formatKey);
                    showLoading();
                    ServiceExecutor.getInstance().execute(new Runnable() {

                        @Override
                        public void run() {

                            PackService packService = new PackServiceImpl();
                            Pack pack = new Pack();
                            pack.setPackKey(formatKey);
                            pack.setLastSaveTime(new Date());
                            pack.setStatus(Constants.DB.NOT_SYNC);
                            packService.addPack(pack);

                            ProductService productService = new ProductServiceImpl();
                            for (int i = 0; i < productKeyList.size(); i++) {
                                Product product = new Product();
                                product.setLastSaveTime(new Date());
                                product.setPackId(pack.getId());
                                product.setProductKey(productKeyList.get(i));
                                productService.addProduct(product);
                            }
                            Message message = Message.obtain();
                            message.what = PACK_SUCCESS_MSG;
                            mHandler.sendMessage(message);

                        }
                    });


                } catch (NotVerifyException e) {
                    ToastMessageHelper.showErrorMessage(PackScanActivity.this, e.getMessage(), true);
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = MSG_FAILURE;
                    mHandler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });

        builder.setView(view);
        packAlertDialog=builder.create();
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
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "打包失败，请检查", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
            }
        }
    };


    private void doAfterPack() {
        ToastMessageHelper.showMessage(PackScanActivity.this,R.string.pack_finish,true);
        packCount++;
        productKeyList.clear();
        et_get_product_key.requestFocus();

        refreshUI();
    }

//    public void onPause() {
//        super.onPause();
//        try {
//            SharedPreferences mySharedPreferences = getSharedPreferences("MainActivity",
//                    Activity.MODE_PRIVATE);
//            SharedPreferences.Editor editor = mySharedPreferences.edit();
//            editor.clear();
//            editor.putString("ItemSize", String.valueOf(productKeyList.size()));
//            for (int i = 0; i < productKeyList.size(); i++) {
//                editor.putString(String.valueOf(i),productKeyList.get(i) );
//            }
//            editor.commit();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

}
