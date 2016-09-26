package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.PatternInfo;
import com.yunsu.manager.FileManager;
import com.yunsu.manager.SettingManager;
import com.yunsu.sqlite.service.PatternService;
import com.yunsu.sqlite.service.impl.PatternServiceImpl;

import java.lang.reflect.Field;

public class GlobalSettingActivity extends BaseActivity {
    private TitleBar titleBar;
    private Button btn_authorize_status;
    private RelativeLayout rl_auto_sync;
    private TextView tv_time_gap;
    private CheckBox cb_auto_inbound;
    private boolean autoInbound;

    @ViewById(id = R.id.tv_cache_size)
    private TextView tv_cache_size;

    @ViewById(id = R.id.rl_clear_cache)
    private RelativeLayout rl_clear_cache;


    @ViewById(id = R.id.rl_product_key_type)
    private RelativeLayout rl_product_key_type;

    @ViewById(id = R.id.rl_pack_key_type)
    private RelativeLayout rl_pack_key_type;

    @ViewById(id = R.id.tv_pack_pattern_value)
    private TextView tv_pack_pattern_value;

    @ViewById(id = R.id.tv_product_pattern_value)
    private TextView tv_product_pattern_value;

    private final int CLEAR_SUCCESS=1;

    private PatternService patternService;

    private static final int QUERY_PACK_PATTERN_MSG=256;

    private static final int QUERY_PRODUCT_PATTERN_MSG=271;

    private static final int RESTORE_SETTING_MSG=168;

    public static final String PATTERN_ID="pattern_id";

    private static final long FALSE_SETTING= -1;

    private  PatternInfo productPatternInfo;

    private  PatternInfo packPatternInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_setting);
        init();
    }

    private void init() {
        getActionBar().hide();

        patternService=new PatternServiceImpl();

        titleBar = (TitleBar) findViewById(R.id.global_setting_title_bar);
        titleBar.setTitle(getString(R.string.settings));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        btn_authorize_status = (Button) findViewById(R.id.btn_authorize_status);
        rl_auto_sync= (RelativeLayout) findViewById(R.id.rl_auto_sync);
        tv_time_gap= (TextView) findViewById(R.id.tv_time_gap);
        int syncMinute= SettingManager.getInstance().getSyncRateMin();
        tv_time_gap.setText("每隔"+ syncMinute+"分钟");
        rl_auto_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(SettingManager.getInstance().getSyncRateMin());
            }
        });
        initAutoInboundCheckBox();
        rl_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(GlobalSettingActivity.this);
                builder.setTitle(R.string.clear_cache);
                builder.setMessage(R.string.clear_cache_release);
                builder.setNegativeButton(R.string.cancel,null);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoading();
                        clearCacheFile();
                    }
                });
                builder.show();
            }
        });

        rl_product_key_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GlobalSettingActivity.this,KeyTempleListActivity.class);
                startActivity(intent);
//                intent.putExtra(Constants.PackPreference.PATTERN,Constants.PackPreference.PRODUCT_PATTERN);
//                startActivityForResult(intent,KeyTempleListActivity.PRODUCT_REQUEST);
            }
        });
        rl_pack_key_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GlobalSettingActivity.this,KeyTempleListActivity.class);
                intent.putExtra(Constants.PackPreference.PATTERN,Constants.PackPreference.PACK_PATTERN);
                startActivityForResult(intent,KeyTempleListActivity.PACK_REQUEST);
            }
        });

        restoreSetting();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==KeyTempleListActivity.PACK_REQUEST && resultCode == KeyTempleListActivity.PATTERN_RESULT ){
            final long patternInfoId=data.getLongExtra(PATTERN_ID,0);

            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    packPatternInfo= patternService.queryPatternById(patternInfoId);
                    handler.sendEmptyMessage(QUERY_PACK_PATTERN_MSG);
                }
            });
        }

        if (requestCode==KeyTempleListActivity.PRODUCT_REQUEST && resultCode == KeyTempleListActivity.PATTERN_RESULT ){
            final long patternInfoId=data.getLongExtra(PATTERN_ID,0);

            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    productPatternInfo= patternService.queryPatternById(patternInfoId);
                    handler.sendEmptyMessage(QUERY_PRODUCT_PATTERN_MSG);
                }
            });
        }

    }

    private void restoreSetting(){
        showLoading();
        SharedPreferences preferences=getSharedPreferences(Constants.PackPreference.PACK_SETTING,MODE_PRIVATE);
        final long temProductPatternId=preferences.getLong(Constants.PackPreference.PRODUCT_PATTERN_ID,FALSE_SETTING);
        final long tempPackPatternId=preferences.getLong(Constants.PackPreference.PACK_PATTERN_ID,FALSE_SETTING);
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (temProductPatternId!=FALSE_SETTING){
                    productPatternInfo=patternService.queryPatternById(temProductPatternId);
                }
                if (tempPackPatternId!=FALSE_SETTING){
                    packPatternInfo=patternService.queryPatternById(tempPackPatternId);
                }
                handler.sendEmptyMessage(RESTORE_SETTING_MSG);
            }
        });

    }

    @Override
    protected void onPause() {
        saveSetting();
        YunsuKeyUtil.getInstance().storePackKeyPattern(packPatternInfo.getRegex());
        YunsuKeyUtil.getInstance().storeProductKeyPattern(productPatternInfo.getRegex());
        super.onPause();
    }

    private void saveSetting(){
        SharedPreferences.Editor editor=getSharedPreferences(Constants.PackPreference.PACK_SETTING,MODE_PRIVATE).edit();
        if (packPatternInfo!=null){
            editor.putLong(Constants.PackPreference.PACK_PATTERN_ID,packPatternInfo.getId());
        }
        if (productPatternInfo!=null){
            editor.putLong(Constants.PackPreference.PRODUCT_PATTERN_ID,productPatternInfo.getId());
        }
        editor.apply();
    }

    private void clearCacheFile() {
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                FileManager.getInstance().clearCache();
                Message message=Message.obtain();
                message.what=CLEAR_SUCCESS;
                handler.sendMessage(message);
            }
        });
    }


    private void setCacheSize() {
        long size = FileManager.getInstance().getAllCacheSize();
        if (size<=0){
            tv_cache_size.setText("0KB");
        }
        if (size>0&&size<=1024){
            tv_cache_size.setText("1KB");
        }
        else if (size < 1024 * 1024&&size>1024) {
            long kb = size / 1024;
            tv_cache_size.setText(String.valueOf(kb) + "KB");
        } else {
            long mb = size / (1024 * 1024);
            tv_cache_size.setText(String.valueOf(mb) + "MB");
        }
    }

    private void initAutoInboundCheckBox() {
        cb_auto_inbound= (CheckBox) findViewById(R.id.cb_auto_inbound);
        autoInbound=SettingManager.getInstance().isAutoInbound();
        cb_auto_inbound.setChecked(autoInbound);
        cb_auto_inbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=null;
                if (autoInbound){
                    message=getString(R.string.cancel_auto_inbound);
                }else {
                    message=getString(R.string.confirm_auto_inbound);
                }
                AlertDialog dialog = new AlertDialog.Builder(GlobalSettingActivity.this).setMessage(message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                autoInbound=!autoInbound;
                                SettingManager.getInstance().saveAutoInboundSetting(autoInbound);
                                cb_auto_inbound.setChecked(autoInbound);
                            }
                        }).setNegativeButton(R.string.no, null).create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });

    }


    private void dialog(int syncMinute){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.please_input_sync_rate);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_ll_tv,null);
        final EditText et= (EditText) view.findViewById(R.id.et_pack_key);
        et.setText(String.valueOf(syncMinute));
        et.setSelection(et.getText().length());
        builder.setView(view);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean closeDialog;
                String numString=et.getText().toString();
                if (numString.startsWith("0")){
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(GlobalSettingActivity.this,"请输入合法的数字",true);
                }else if (numString.length()<=3&&(Integer.parseInt(numString)<=120)){
                    closeDialog=true;
                    int min=Integer.parseInt(numString);
                    SettingManager.getInstance().saveSyncRateSetting(min);
                    tv_time_gap.setText("每隔"+ min+"分钟");
                }else {
                    closeDialog=false;
                    ToastMessageHelper.showErrorMessage(GlobalSettingActivity.this,"请输入120分钟以内的数字",true);
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

    private void setAuthorizeStatus() {

        btn_authorize_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(GlobalSettingActivity.this).setTitle(R.string.confirm_cancel_authorize).setMessage(R.string.cancel_authorize_message)
                        .setPositiveButton(R.string.confirm_cancel_authorize, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putBoolean("isAuthorize", false);
                                editor.commit();
                                SessionManager.getInstance().logout();
                                Intent intent=new Intent(GlobalSettingActivity.this,AuthorizeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton(R.string.no, null).create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });

    }

    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case   CLEAR_SUCCESS:
                    hideLoading();
                    setCacheSize();
                    break;

                case QUERY_PACK_PATTERN_MSG:
                    refreshUI();
                    break;

                case QUERY_PRODUCT_PATTERN_MSG:
                    refreshUI();
                    break;

                case RESTORE_SETTING_MSG:
                    hideLoading();
                    refreshUI();

                default:

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private  void refreshUI(){
        if (packPatternInfo!=null){
            tv_pack_pattern_value.setText(packPatternInfo.getName());
        }

        if (productPatternInfo!=null){
            tv_product_pattern_value.setText(productPatternInfo.getName());
        }

    }

}
