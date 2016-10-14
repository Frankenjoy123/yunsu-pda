package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.exception.NotVerifyException;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.manager.LogisticManager;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    @ViewById(id = R.id.btn_confirm_finish)
    Button btn_confirm_finish;

    @ViewById(id = R.id.btn_revoke_outbound)
    Button btn_revoke_outbound;

    @ViewById(id = R.id.tv_progress_status)
    TextView tv_progress_status;

    @ViewById(id = R.id.ll_scan_key)
    LinearLayout ll_scan_key;

    @ViewById(id = R.id.tv_create_time)
    TextView tv_create_time;
    
    private MaterialService materialService;

    private PackService packService;
    
    private Material material;

    private long sendCount;

    private static final int QUERY_MATERIAL_SUCCESS=100;

    private static final int INSERT_PACK_SUCCESS=200;

    private static final int CONFIRM_FINISH_ORDER_MSG=303;

    private static final int EXIST_IN_ORDER_MSG=123;

    private static final int REVOKE_KEY_IN_OTHER_ORDER_MSG=125;

    private  long id;

    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_scan);
        basicInit();
    }

    private void basicInit() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.outbound_scan));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setRightButtonText(getString(R.string.done));

        id=getIntent().getLongExtra(Constants.DB.ID,0);
        materialService=new MaterialServiceImpl();
        packService=new PackServiceImpl();
        bindRevokeOrder();
        bindConfirmFinishOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        showLoading();

        if (id!=0){
            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    material= materialService.queryById(id);
                    handler.sendEmptyMessage(QUERY_MATERIAL_SUCCESS);
                }
            });
        }

        bindTextChanged();

    }

    private void bindRevokeOrder() {
        btn_revoke_outbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderScanActivity.this, OrderRevokeActivity.class);
                intent.putExtra(Constants.DB.ID, id);
                startActivity(intent);
            }
        });
    }

    private void bindConfirmFinishOrder() {
        btn_confirm_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(OrderScanActivity.this).setTitle(R.string.confirm_finish).setMessage(R.string.confirm_finish_message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showLoading();
                                ServiceExecutor.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        material.setProgressStatus(Constants.DB.FINISHED);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        material.setFinishTime(dateFormat.format(new Date()));
                                        materialService.updateMaterial(material);
                                        handler.sendEmptyMessage(CONFIRM_FINISH_ORDER_MSG);
                                    }
                                });
                            }
                        }).setNegativeButton(R.string.cancel, null).create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    private void refreshUI(){
        hideLoading();
        tv_agency_name.setText(material.getAgencyName());;
        tv_order_id.setText(String.valueOf(material.getId()));
        tv_create_time.setText(material.getCreateTime());
        tv_outbound_amount.setText(String.valueOf(material.getAmount()));
        tv_outbound_count.setText(String.valueOf(material.getSent()));
        String progressStatus=null;
        int color=0;
        switch (material.getProgressStatus()){
            case  Constants.DB.NOT_START:
                progressStatus=getString(R.string.not_start);
                color=   getResources().getColor(R.color.order_list_not_start);
                btn_confirm_finish.setEnabled(false);
                btn_revoke_outbound.setEnabled(false);
                et_scan.setEnabled(true);
                ll_scan_key.setVisibility(View.VISIBLE);
                break;
            case Constants.DB.IN_PROGRESS:
                progressStatus=getString(R.string.in_progress);
                color=getResources().getColor(R.color.order_list_in_progress);
                btn_confirm_finish.setEnabled(true);
                btn_revoke_outbound.setEnabled(true);
                et_scan.setEnabled(true);
                ll_scan_key.setVisibility(View.VISIBLE);
                break;
            case Constants.DB.FINISHED:
                progressStatus=getString(R.string.finished);
                color=getResources().getColor(R.color.order_list_finish);
                btn_confirm_finish.setEnabled(false);
                btn_revoke_outbound.setEnabled(true);
                et_scan.setEnabled(false);
                ll_scan_key.setVisibility(View.INVISIBLE);
                break;
            default:
                progressStatus=getString(R.string.not_start);
                color=   getResources().getColor(R.color.order_list_not_start);
                btn_confirm_finish.setEnabled(false);
                et_scan.setEnabled(true);
                ll_scan_key.setVisibility(View.VISIBLE);
                break;
        }

        tv_progress_status.setText(progressStatus);
        tv_progress_status.setTextColor(color);
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
                if (StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                if (material.getProgressStatus()==Constants.DB.FINISHED){
                    return;
                }
                try {
                    String formatKey=YunsuKeyUtil.getInstance().verifyPackageKey(string);
                    submitToDB(formatKey);
                    tv_scan_key.setText(formatKey);
                } catch (NotVerifyException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage() , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }finally {
                    et_scan.setText("");
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
                Pack queryPack=new Pack();
                queryPack.setActionId(Constants.Logistic.OUTBOUND_CODE);
                queryPack.setPackKey(packKey);
                Pack resultPack=packService.queryRevokeOrNot(queryPack);

                //包装不存在于任何订单中
                if (resultPack==null){
                    Pack pack=new Pack();
                    pack.setPackKey(packKey);
                    pack.setStatus(Constants.DB.NOT_SYNC);
                    pack.setActionId(Constants.Logistic.OUTBOUND_CODE);
                    pack.setAgency(material.getAgencyId());
                    pack.setMaterialId(material.getId());
                    pack.setSaveTime(dateFormat.format(new Date()));
                    pack.setMaterialId(material.getId());
                    pack.setAgency(material.getAgencyId());
                    packService.insertPackData(pack);

                    updateMaterialIncrease();

                    handler.sendEmptyMessage(INSERT_PACK_SUCCESS);
                }
                else {
                    //包装码已经存在于当前订单中
                    if (resultPack.getMaterialId()==material.getId()){

                        //在当前订单中已被撤销
                        if (resultPack.getStatus().equals(Constants.Logistic.REVOKE_OUTBOUND_CODE)){
                            resultPack.setActionId(Constants.Logistic.OUTBOUND_CODE);
                            resultPack.setStatus(Constants.DB.NOT_SYNC);
                            resultPack.setSaveTime(dateFormat.format(new Date()));
                            packService.updatePack(resultPack);

                            updateMaterialIncrease();
                            handler.sendEmptyMessage(INSERT_PACK_SUCCESS);

                        }else {//在当前订单中未被撤销过
                            handler.sendEmptyMessage(EXIST_IN_ORDER_MSG);
                        }

                    //包装码存在于其他订单中
                    }else {

                        // 码在其他订单中，已被撤销
                        if (resultPack.getActionId().equals(Constants.Logistic.REVOKE_OUTBOUND_CODE)){
                            resultPack.setMaterialId(material.getId());
                            resultPack.setActionId(Constants.Logistic.OUTBOUND_CODE);
                            resultPack.setStatus(Constants.DB.NOT_SYNC);
                            resultPack.setSaveTime(dateFormat.format(new Date()));
                            resultPack.setAgency(material.getAgencyId());
                            packService.updatePack(resultPack);

                            updateMaterialIncrease();
                            handler.sendEmptyMessage(INSERT_PACK_SUCCESS);

                        }
                        //码在其他订单中，未被撤销
                        else {

                            Message message=Message.obtain();
                            message.obj=resultPack.getMaterialId();
                            message.what=REVOKE_KEY_IN_OTHER_ORDER_MSG;
                            handler.sendMessage(message);

                        }
                    }
                }

            }
        });
    }

    //物料订单加1更新
    private void updateMaterialIncrease() {
        material.setSent(material.getSent()+1);
        if (material.getSent()<material.getAmount()){
            material.setProgressStatus(Constants.DB.IN_PROGRESS);
        }else {
            material.setProgressStatus(Constants.DB.FINISHED);
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            material.setFinishTime(dateFormat.format(new Date()));
            LogisticManager.getInstance().createOutOrderFile( material,materialService.queryPacks(material));
        }
        materialService.updateMaterial(material);
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

                case EXIST_IN_ORDER_MSG:
                    ToastMessageHelper.showMessage(OrderScanActivity.this,
                            R.string.key_exist_in_this_order,true);
                    break;

                case REVOKE_KEY_IN_OTHER_ORDER_MSG:
                    ToastMessageHelper.showMessage(OrderScanActivity.this,getString(R.string.key_exist_in_other_order)+msg.obj,true);
                    break;

                case CONFIRM_FINISH_ORDER_MSG:
                    hideLoading();
                    finish();
                    break;

            }
        }
    };

}
