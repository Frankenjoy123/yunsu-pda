package com.yunsu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
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
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import org.apache.log4j.Logger;

public class OrderRevokeActivity extends BaseActivity {

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

    private long revokeCount=0;

    private static final int QUERY_MATERIAL_SUCCESS=100;

    private static final int REVOKE_SUCCESS_MSG=401;
    private static final int KEY_NOT_EXIST_MSG =402;
    private static final int HAS_REVOKED_MSG = 403;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_revoke);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.repeal_outbound));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);

        final long id=getIntent().getLongExtra(Constants.DB.ID,0);
        materialService=new MaterialServiceImpl();
        packService=new PackServiceImpl();

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

    private void refreshUI(){
        hideLoading();
        tv_agency_name.setText(material.getAgencyName());
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
                if (StringHelper.isStringNullOrEmpty(string)){
                    return;
                }
                try {
                    String formatKey=YunsuKeyUtil.getInstance().verifyPackageKey(string);
                    checkKeyStatus(formatKey);
                    tv_scan_key.setText(formatKey);

                } catch (NotVerifyException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage() , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }catch (Exception e) {
                    Logger logger=Logger.getLogger(OrderRevokeActivity.class);
                    logger.error(e.getMessage());
                }finally {
                    et_scan.setText("");
                }

            }
        });
    }

    /**
     * 检查码的状态
     * @param key
     */
    private void checkKeyStatus(final String key){

        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Pack queryPack=new Pack();
                queryPack.setPackKey(key);
                queryPack.setMaterialId(material.getId());
                Pack resultPack=packService.queryByKeyMaterialId(queryPack);

                if (resultPack!=null){

                    if (resultPack.getActionId().equals(Constants.Logistic.OUTBOUND_CODE)){
                        packService.revokePackInOrder(resultPack);
                        material.setSent(material.getSent()-1);
                        if(material.getSent()==0){
                            material.setProgressStatus(Constants.DB.NOT_START);
                        }else {
                            material.setProgressStatus(Constants.DB.IN_PROGRESS);
                        }
                        materialService.updateMaterial(material);
                        handler.sendEmptyMessage(REVOKE_SUCCESS_MSG);
                    }else {
                        handler.sendEmptyMessage(HAS_REVOKED_MSG);
                    }

                }else {
                    //返回扫描不存在
                    handler.sendEmptyMessage(KEY_NOT_EXIST_MSG);
                }
            }
        });
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_MATERIAL_SUCCESS:

                case REVOKE_SUCCESS_MSG:
                    hideLoading();
                    refreshUI();
                    break;

                case HAS_REVOKED_MSG:
                    ToastMessageHelper.showMessage(OrderRevokeActivity.this,"当前订单中的包装已被撤销出库，请检查",true);
                    break;

                case KEY_NOT_EXIST_MSG:
                    ToastMessageHelper.showMessage(OrderRevokeActivity.this,"该包装不存在于当前订单中，请检查",true);
                    break;
            }
        }
    };

}
