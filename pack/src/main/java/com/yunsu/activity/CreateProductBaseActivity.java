package com.yunsu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.StringHelper;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.sqlite.service.ProductBaseService;
import com.yunsu.sqlite.service.impl.ProductBaseServiceImpl;

public class CreateProductBaseActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    private TitleBar titleBar;

    @ViewById(id = R.id.et_product_name)
    private EditText et_product_name;

    @ViewById(id = R.id.et_product_number)
    private EditText et_product_number;

    @ViewById(id = R.id.btn_create_product)
    private Button btn_create_product;

    @ViewById(id = R.id.rl_root_create_product)
    private RelativeLayout rl_root_create_product;

    private ProductBaseService productBaseService =new ProductBaseServiceImpl();

    private static final int INSERT_NEW_PRODUCT_MSG=309;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product_base);
        init();
    }


    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.staff_list));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);

        btn_create_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String productName= et_product_name.getText().toString();
                final String productNumber=et_product_number.getText().toString();
                if (StringHelper.isStringNullOrEmpty(productName)){
                    ToastMessageHelper.showErrorMessage(CreateProductBaseActivity.this,R.string.product_name_not_null,true);
                }else if (StringHelper.isStringNullOrEmpty(productNumber)){
                    ToastMessageHelper.showErrorMessage(CreateProductBaseActivity.this,R.string.product_number_not_null,true);
                }else {
                    ServiceExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            ProductBase productBase=new ProductBase(null,productNumber,productName);
                            long id=productBaseService.insert(productBase);
                            Message message=Message.obtain();
                            message.what=INSERT_NEW_PRODUCT_MSG;
                            message.obj=id;
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        });

        rl_root_create_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==INSERT_NEW_PRODUCT_MSG){
                Intent intent=getIntent();
                intent.putExtra(Constants.PRODUCT_BASE_ID, (long) msg.obj);
                setResult(ProductBaseListActivity.CREATE_NEW_RESULT,intent);
                finish();
            }
        }
    };
}
