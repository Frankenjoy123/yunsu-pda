package com.yunsu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.adapter.OrderAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringUtils;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderScanActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.tv_order_id)
    TextView tv_order_id;

    @ViewById(id = R.id.tv_agency_name)
    TextView tv_agency_name;

    @ViewById(id = R.id.tv_outbound_count)
    TextView tv_outbound_count;

    @ViewById(id = R.id.tv_outbound_amount)
    TextView tv_outbound_amount;

    @ViewById(id = R.id.tv_scan_key)
    TextView tv_scan_key;

    @ViewById(id = R.id.et_scan)
    EditText et_scan;
    
    private MaterialService materialService;

    private PackService packService;
    
    private Material material;

    private long sendCount;

    private static final int QUERY_MATERIAL_SUCCESS=100;

    private static final int INSERT_PACK_SUCCESS=200;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_scan);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.outbound_scan));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.done));
        
        final long id=getIntent().getLongExtra(OrderListActivity.ID,0);
        materialService=new MaterialServiceImpl();
        packService=new PackServiceImpl();

        showLoading();
        
        if (id!=0){
            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    material= materialService.queryById(id);
                    sendCount=material.getSent();
                    Message message=Message.obtain();
                    message.what=QUERY_MATERIAL_SUCCESS;
                    handler.sendEmptyMessage(QUERY_MATERIAL_SUCCESS);
                }
            });
        }

        bindTextChanged();

    }

    private void refreshUI(){
        hideLoading();
        tv_agency_name.setText(material.getAgencyName());;
        tv_order_id.setText(String.valueOf(material.getId()));
        tv_outbound_amount.setText(String.valueOf(material.getAmount()));
        tv_outbound_count.setText(String.valueOf(material.getSent()));
    }


    /**
     * 扫码事件
     */
    private void bindTextChanged(){
        et_scan.requestFocus();
        et_scan.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string=new StringBuilder(s).toString();
                Pattern pattern =Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");;
                Matcher matcher=pattern.matcher(string);
                if (matcher.find()){
                    String formatKey= StringUtils.replaceBlank(StringUtils.getLastString(string));
                    try {

                        if(string.isEmpty()||string=="\n"){
                            return;
                        }
                        submitToDB(formatKey);

                        tv_scan_key.setText(formatKey);
                    } catch (Exception e) {
                        Logger logger=Logger.getLogger(OrderScanActivity.class);
                        logger.error(e.getMessage());
                    }
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.key_not_verify) , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }

            }
        });
    }

    /**
     * 将扫描的key结果存储到DB
     * @param packKey
     */
    private void submitToDB(final String packKey) {
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Pack pack=new Pack();
                pack.setPackKey(packKey);
                pack.setStatus(Constants.DB.NOT_SYNC);
                pack.setActionId(Constants.Logistic.OUTBOUND_CODE);
                pack.setAgency(material.getAgencyId());
                pack.setMaterial(material);
                pack.setMaterialId(material.getId());
                packService.insertPackWithCheck(pack);

                material.setSent(material.getSent()+1);
                if (material.getSent()<material.getAmount()){
                    material.setProgressStatus(Constants.DB.IN_PROGRESS);
                }else {
                    material.setProgressStatus(Constants.DB.FINISHED);
                }
                materialService.updateMaterial(material);

                handler.sendEmptyMessage(INSERT_PACK_SUCCESS);

            }
        });
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_MATERIAL_SUCCESS:
                    hideLoading();
                    refreshUI();
                    break;
                case INSERT_PACK_SUCCESS:
                    refreshUI();

                    if (material.getSent()==material.getAmount()){
                        AlertDialog dialog = new AlertDialog.Builder(OrderScanActivity.this).setTitle(R.string.order_finish).setMessage(R.string.order_finish_message)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).create();
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                    break;
            }
        }
    };

}
