package com.yunsu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yunsu.common.annotation.ViewById;
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

    private ProductBaseService productBaseService =new ProductBaseServiceImpl();

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
                String productName= et_product_name.getText().toString();
                String productNumber=et_product_number.getText().toString();
                if (StringHelper.isStringNullOrEmpty(productName)){
                    ToastMessageHelper.showErrorMessage(CreateProductBaseActivity.this,R.string.product_name_not_null,true);
                }else if (StringHelper.isStringNullOrEmpty(productNumber)){
                    ToastMessageHelper.showErrorMessage(CreateProductBaseActivity.this,R.string.product_number_not_null,true);
                }else {
                    ProductBase productBase=new ProductBase(null,productNumber,productName);
                    productBaseService.insert(productBase);
                    finish();
                }
            }
        });
    }
}
