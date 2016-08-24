package com.yunsu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.network.ImageViewInfo;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.rl_agent_name)
    private RelativeLayout rl_agent_name;

    @ViewById(id = R.id.rl_order_amount)
    private RelativeLayout rl_order_amount;

    @ViewById(id = R.id.tv_agent_name)
    private TextView tv_agent_name;

    @ViewById(id = R.id.tv_order_amount)
    private TextView tv_order_amount;

    @ViewById(id = R.id.btn_confirm_create)
    private Button btn_confirm_create;

    public static final int SUCCESS=100;

    private static final int DEFAULT_AMOUNT=1;

    private String agentId;
    private String agentName;

    private MaterialService materialService;

    private List<Material> materialList=new ArrayList<>();

    private static final int INSERT_SUCCESS=11;

    public static final int GET_AGENCY_QUEST=101;

    public static final int GET_AGENCY_RESULT=201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_definition);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.create_new_order));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setRightButtonText(getString(R.string.done));

        tv_order_amount.setText(String.valueOf(DEFAULT_AMOUNT));

        materialService=new MaterialServiceImpl();

        btn_confirm_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (agentId!=null&&agentName!=null){
                    showLoading();
                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {

                            int amount=Integer.parseInt(tv_order_amount.getText().toString());
                            Material material=new Material();
                            material.setAmount((long) amount);
                            material.setSent((long) 0);
                            material.setProgressStatus(Constants.DB.NOT_START);
                            material.setSyncStatus(Constants.DB.NOT_SYNC);
                            material.setAgencyId(agentId);
                            material.setAgencyName(agentName);
                            materialService.insertMaterial(material);
                            handler.sendEmptyMessage(INSERT_SUCCESS);
                        }
                    });
                }else {
                    ToastMessageHelper.showMessage(CreateOrderActivity.this,R.string.please_choose_org_agency,true);
                }
            }
        });

        rl_agent_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CreateOrderActivity.this,OrgAgencyActivity.class);
                startActivityForResult(intent,GET_AGENCY_QUEST);
            }
        });


        rl_order_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString=tv_order_amount.getText().toString();
                int amount=Integer.parseInt(numString);
                dialog(amount);
            }
        });
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case INSERT_SUCCESS:
                    hideLoading();
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==GET_AGENCY_RESULT){
            agentId=data.getStringExtra(Constants.Logistic.AGENCY_ID);
            agentName=data.getStringExtra(Constants.Logistic.AGENCY_NAME);
            if (agentName!=null){
                tv_agent_name.setText(agentName);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 设置发货数
     * @param amount 发货数
     */
    private void dialog(int amount){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.please_type_in_amount);
        LayoutInflater inflater=getLayoutInflater();
        final View view=inflater.inflate(R.layout.dialog_delivery_amount,null);
        final EditText et= (EditText) view.findViewById(R.id.et_amount);
        et.setText(String.valueOf(amount));
        et.setSelection(et.getText().length());
        builder.setView(view);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean closeDialog;
                String numString=et.getText().toString();
                if (numString.startsWith("0")){
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(CreateOrderActivity.this,"请输入合法的数字",true);
                }else if (numString.length()<=6&&(Integer.parseInt(numString)<=100000)){
                    closeDialog=true;
                    int amount=Integer.parseInt(numString);
                    tv_order_amount.setText(amount+"");
                    InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }else {
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(CreateOrderActivity.this,"请输入十万以内的合法正数",true);
                }
                try {
                    //下面三句控制弹框的关闭
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface,closeDialog);//true表示要关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //下面三句控制弹框的关闭
                    Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialogInterface,true);//true表示要关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }

}
