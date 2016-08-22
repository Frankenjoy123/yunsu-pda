package com.yunsu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.network.ImageViewInfo;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.manager.SettingManager;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;

import java.lang.reflect.Field;
import java.math.BigDecimal;

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

//    @ViewById(id = R.id.ic_minus)
//    private ImageView ic_minus;
//
//    @ViewById(id = R.id.ic_add)
//    private ImageView ic_add;

    public static final int SUCCESS=100;
//    private MiusThread miusThread;
//    private boolean isOnLongClick;
//    private PlusThread plusThread;

    private int amount=1;

    private String agentId;
    private String agentName;

    private MaterialService materialService;

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
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.done));

        materialService=new MaterialServiceImpl();

        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ServiceExecutor.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        String numString=tv_order_amount.getText().toString();
                        int temp=Integer.parseInt(numString);
                        Material material=new Material();
                        material.setAmount((long) temp);
                        material.setAgencyId(agentId);
                        material.setAgencyName(agentName);
                        materialService.insertMaterial(material);
                    }
                });

                Intent intent=getIntent();
                intent.putExtra(Constants.Logistic.AGENCY_ID,agentId);
                intent.putExtra(Constants.Logistic.AGENCY_NAME,agentName);
                setResult(OrderListActivity.ADD_NEW_ORDER_RESULT,intent);
                finish();
            }
        });


        rl_agent_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CreateOrderActivity.this,OrgAgencyActivity.class);
                startActivityForResult(intent,SUCCESS);
            }
        });

        tv_order_amount.setText(String.valueOf(amount));

        rl_order_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString=tv_order_amount.getText().toString();
                int amount=Integer.parseInt(numString);
                dialog(amount);
            }
        });

//        ic_minus.setEnabled(false);
//
//        ic_minus.setOnTouchListener(new ComponentOnTouch());
//        ic_add.setOnTouchListener(new ComponentOnTouch());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        agentId=data.getStringExtra(Constants.Logistic.AGENCY_ID);
        agentName=data.getStringExtra(Constants.Logistic.AGENCY_NAME);
        tv_agent_name.setText(agentName);
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
        View view=inflater.inflate(R.layout.dialog_delivery_amount,null);
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



//    //Touch事件
//    class ComponentOnTouch implements View.OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (v.getId()) {
//                //这是btnMius下的一个层，为了增强易点击性
//                case R.id.ic_minus:
//                    onTouchChange("mius", event.getAction());
//                    break;
//                //这里也写，是为了增强易点击性
////                case R.id.btnMius:
////                    onTouchChange("mius", event.getAction());
////                    break;
////                case R.id.linearBtnPlus:
////                    onTouchChange("plus", event.getAction());
////                    break;
//                case R.id.ic_add:
//                    onTouchChange("plus", event.getAction());
//                    break;
//            }
//            return true;
//        }
//    }
//
//    private void onTouchChange(String methodName, int eventAction) {
//        //按下松开分别对应启动停止减线程方法
//        if ("mius".equals(methodName)) {
//            if (eventAction == MotionEvent.ACTION_DOWN) {
//                miusThread = new MiusThread();
//                isOnLongClick = true;
//                miusThread.start();
//            } else if (eventAction == MotionEvent.ACTION_UP) {
//                if (miusThread != null) {
//                    isOnLongClick = false;
//                }
//            } else if (eventAction == MotionEvent.ACTION_MOVE) {
//                if (miusThread != null) {
//                    isOnLongClick = true;
//                }
//            }
//        }
//        //按下松开分别对应启动停止加线程方法
//        else if ("plus".equals(methodName)) {
//            if (eventAction == MotionEvent.ACTION_DOWN) {
//                plusThread = new PlusThread();
//                isOnLongClick = true;
//                plusThread.start();
//            } else if (eventAction == MotionEvent.ACTION_UP) {
//                if (plusThread != null) {
//                    isOnLongClick = false;
//                }
//            } else if (eventAction == MotionEvent.ACTION_MOVE) {
//                if (plusThread != null) {
//                    isOnLongClick = true;
//                }
//            }
//        }
//    }
//
//
//    //减操作
//    class MiusThread extends Thread {
//        @Override
//        public void run() {
//            while (isOnLongClick) {
//                try {
//                    Thread.sleep(100);
//                    myHandler.sendEmptyMessage(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                super.run();
//            }
//        }
//    }
//
//    //加操作
//    class PlusThread extends Thread {
//        @Override
//        public void run() {
//            while (isOnLongClick) {
//                try {
//                    Thread.sleep(100);
//                    myHandler.sendEmptyMessage(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                super.run();
//            }
//        }
//    }

//    //更新文本框的值
//    Handler myHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    if (amount>1){
//                        tv_order_amount.setText(String.valueOf(--amount));
//                    }else {
//                        ic_minus.setEnabled(false);
//                    }
//                   ic_add.setEnabled(true);
//                    break;
//
//                case 2:
//                    if (amount<=100000){
//                        tv_order_amount.setText(String.valueOf(++amount));
//                    }else {
//                        ic_add.setEnabled(false);
//                    }
//                    ic_minus.setEnabled(true);
//                    break;
//            }
//
//        };
//    };

}
