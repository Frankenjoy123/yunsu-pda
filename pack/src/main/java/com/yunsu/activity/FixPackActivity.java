package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.util.YunsuKeyUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.entity.PackInfoEntity;
import com.yunsu.greendao.entity.Pack;
import com.yunsu.greendao.entity.Product;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.ProductBaseService;
import com.yunsu.sqlite.service.ProductService;
import com.yunsu.sqlite.service.StaffService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.sqlite.service.impl.ProductBaseServiceImpl;
import com.yunsu.sqlite.service.impl.ProductServiceImpl;
import com.yunsu.sqlite.service.impl.StaffServiceImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FixPackActivity extends BaseActivity {

    @ViewById(id = R.id.fix_title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_get_pack_key)
    private EditText et_get_pack_key;

    @ViewById(id = R.id.tv_staff)
    protected TextView tv_staff;

    @ViewById(id = R.id.tv_standard)
    protected TextView tv_standard;

    @ViewById(id = R.id.tv_standard_in_progress)
    protected TextView tv_standard_in_progress;

    @ViewById(id = R.id.tv_product)
    protected TextView tv_product;

    @ViewById(id = R.id.et_get_product_key)
    protected EditText et_get_product_key;

    @ViewById(id = R.id.tv_scan_key)
    protected TextView tv_scan_key;

    @ViewById(id = R.id.progressBar1)
    protected ProgressBar progressBar;

    @ViewById(id = R.id.tv_product_count)
    private TextView tv_product_count;

    @ViewById(id = R.id.btn_revoke)
    private Button btn_revoke;

    @ViewById(id = R.id.btn_fix_pack_done)
    private Button btn_fix_pack_done;

    @ViewById(id = R.id.rl_total_view)
    private RelativeLayout rl_total_view;

    @ViewById(id = R.id.ll_fix_tip)
    private LinearLayout ll_fix_tip;

    @ViewById(id = R.id.tv_pack_key_in_title)
    private TextView tv_pack_key_in_title;

    @ViewById(id = R.id.ll_scan_key)
    private LinearLayout ll_scan_key;

    @ViewById(id = R.id.tv_scan_error_tip)
    private TextView tv_scan_error_tip;

    private static final int QUERY_PACK_SUCCESS_MSG =145;

    private static final int QUERY_PACK_FAIL_MSG=167;

    private List<String> productKeyList;

    private List<String> originalCodes;

    private ProductService productService;
    private PackService packService;
    private StaffService staffService;
    private ProductBaseService productBaseService;

    private Staff staff;
    private ProductBase productBase;
    private Pack queryResultPack;


    private static final int FIX_PACK_REVOKE_REQUEST=366;

    private static final int FIX_PACK_SUCCESS_MSG=112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_pack);

        init();

    }

    private void init() {
        getActionBar().hide();
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle(getString(R.string.fix_pack));

        packService=new PackServiceImpl();
        productService=new ProductServiceImpl();
        staffService=new StaffServiceImpl();
        productBaseService=new ProductBaseServiceImpl();

        productKeyList =new ArrayList<String>();
        originalCodes=new ArrayList<String>();

        bindPackKeyChanged();

        bindProductKeyChanged();

        bindButtonClicked();
    }

    private void bindButtonClicked() {
        btn_revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackInfoEntity entity=new PackInfoEntity();
                entity.setStaffId(staff.getId());
                entity.setStaffName(staff.getName());
                entity.setStaffNumber(staff.getStaffNumber());
                entity.setProductBaseId(productBase.getId());
                entity.setProductBaseName(productBase.getName());
                entity.setProductBaseNumber(productBase.getProductNumber());
                entity.setStandard(queryResultPack.getStandard());

                Intent intent=new Intent(FixPackActivity.this,RevokeActivity.class);
                intent.putExtra(Constants.PACK_INFO,entity);
                intent.putExtra(Constants.PRODUCT_KEY_LIST, (Serializable) productKeyList);
                startActivityForResult(intent,FIX_PACK_REVOKE_REQUEST);
            }
        });

        btn_fix_pack_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sameKeyCount=compareProductKeyList(productKeyList,originalCodes);
                int addCount=productKeyList.size()-sameKeyCount;
                int deleteCount=originalCodes.size()-sameKeyCount;
                AlertDialog.Builder builder=new AlertDialog.Builder(FixPackActivity.this);
                builder.setTitle(R.string.fix_pack_done);
                builder.setMessage(String.format(getString(R.string.pack_fix_summary)
                        ,originalCodes.size(),productKeyList.size(),addCount,deleteCount));

                builder.setPositiveButton(R.string.fix_pack_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoading();
                        ServiceExecutor.getInstance().execute(new Runnable() {

                            @Override
                            public void run() {
                                SimpleDateFormat format=new SimpleDateFormat(Constants.dateFormat);

                                PackService packService = new PackServiceImpl();
                                queryResultPack.setStatus(Constants.DB.NOT_SYNC);
                                queryResultPack.setLastSaveTime(format.format(new Date()));
                                packService.updatePack(queryResultPack);

                                ProductService productService = new ProductServiceImpl();
                                productService.removeAllProductByPackId(queryResultPack.getId());
                                for(int i = 0; i< productKeyList.size(); i++){
                                    Product product=new Product();
                                    product.setProductKey(productKeyList.get(i));
                                    product.setPackId(queryResultPack.getId());
                                    productService.addProduct(product);
                                }

                                handler.sendEmptyMessage(FIX_PACK_SUCCESS_MSG);
                            }
                        });
                    }
                });

                builder.setNegativeButton(R.string.cancel,null);
                builder.create().show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==FIX_PACK_REVOKE_REQUEST
                && resultCode==RevokeActivity.REVOKE_PACK_RESULT){
            ArrayList<String> list= (ArrayList<String>) data.getSerializableExtra(Constants.PRODUCT_KEY_LIST);
            productKeyList.clear();
            productKeyList.addAll(list);
            refreshUI();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                String string = new StringBuilder(editable).toString();
                try {
                    final String formatKey=YunsuKeyUtil.getInstance().verifyPackageKey(string);

                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            queryResultPack = packService.QueryPack(formatKey);

                            if (queryResultPack!=null){

                                staff=staffService.queryStaffById(queryResultPack.getStaffId());
                                productBase=productBaseService.queryProductBaseById(queryResultPack.getProductBaseId());

                                List<Product> resultProductList=productService.queryAllProductByPackId(queryResultPack.getId());

                                if (resultProductList!=null){
                                    for (int i=0;i<resultProductList.size();i++) {
                                        productKeyList.add(resultProductList.get(i).getProductKey());
                                    }
                                    originalCodes.addAll(productKeyList);
                                }

                                handler.sendEmptyMessage(QUERY_PACK_SUCCESS_MSG);
                            }else {
                                handler.sendEmptyMessage(QUERY_PACK_FAIL_MSG);
                            }
                        }
                    });

                } catch (Exception e) {
                    ToastMessageHelper.showErrorMessage(FixPackActivity.this,e.getMessage(),true);
                }
            }
        });
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_PACK_SUCCESS_MSG:
                    rl_total_view.setVisibility(View.VISIBLE);
                    ll_fix_tip.setVisibility(View.GONE);
                    tv_pack_key_in_title.setText(queryResultPack.getPackKey());
                    if (staff!=null){
                        tv_staff.setText(staff.getName());
                    }
                    if (productBase!=null){
                        tv_product.setText(productBase.getName());
                    }
                    tv_standard.setText(String.valueOf(queryResultPack.getStandard()));
                    tv_standard_in_progress.setText(String.valueOf(queryResultPack.getStandard()));
                    progressBar.setMax(queryResultPack.getStandard());

                    refreshUI();
                    break;

                case QUERY_PACK_FAIL_MSG:
                    ToastMessageHelper.showErrorMessage(FixPackActivity.this,R.string.not_find_pack,true);
                    break;

                case FIX_PACK_SUCCESS_MSG:
                    hideLoading();
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void refreshUI() {
        progressBar.setProgress(productKeyList.size());
        tv_product_count.setText(String.valueOf(productKeyList.size()));
        if (productKeyList.size() == 0) {
            btn_fix_pack_done.setEnabled(false);
            btn_revoke.setEnabled(false);
        } else {
            btn_fix_pack_done.setEnabled(true);
            btn_revoke.setEnabled(true);
        }
    }


    private void bindProductKeyChanged() {
        et_get_product_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(productKeyList.size()<queryResultPack.getStandard()){
                    String string = new StringBuilder(s).toString();
                    try {
                        String formatKey=YunsuKeyUtil.getInstance().verifyProductKey(string);
                        if (!productKeyList.contains(formatKey)){
                            productKeyList.add(formatKey);
                            ll_scan_key.setVisibility(View.VISIBLE);
                            tv_scan_error_tip.setVisibility(View.INVISIBLE);
                            tv_scan_key.setText(formatKey);
                            refreshUI();
                        }else {
                            ll_scan_key.setVisibility(View.INVISIBLE);
                            tv_scan_error_tip.setVisibility(View.VISIBLE);
                            tv_scan_error_tip.setText(R.string.product_key_exist_in_this_pack);
                        }

                    } catch (Exception e) {
                        ToastMessageHelper.showErrorMessage(getApplication(),e.getMessage(),true);
                    }
                }
                else{
                    ToastMessageHelper.showMessage(getApplication(),R.string.pack_fill_up,true);
                }

            }
        });
    }

    private int compareProductKeyList(List<String> current, List<String> origin){
        int  originProductCount=0;
        for(String key : current){
            if (origin.contains(key)){
                originProductCount++;
            }
        }
        return originProductCount;
    }

    @Override
    public void onBackPressed() {
        if (queryResultPack != null){
            int sameKeyCount=compareProductKeyList(productKeyList,originalCodes);
            int addCount=productKeyList.size()-sameKeyCount;
            int deleteCount=originalCodes.size()-sameKeyCount;

            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle(R.string.want_to_to_back);
            builder.setMessage(String.format(getString(R.string.pack_fix_summary)
                    ,originalCodes.size(),productKeyList.size(),addCount,deleteCount));
            builder.setNegativeButton(R.string.click_wrong,null);
            builder.setPositiveButton(R.string.still_go_back, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();
        }else {
            super.onBackPressed();
        }
    }
}
