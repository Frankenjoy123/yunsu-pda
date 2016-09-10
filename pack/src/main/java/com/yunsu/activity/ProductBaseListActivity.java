package com.yunsu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsu.adapter.ProductBaseAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.sqlite.service.ProductBaseService;
import com.yunsu.sqlite.service.impl.ProductBaseServiceImpl;

import java.util.List;

public class ProductBaseListActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_product_base)
    ListView lv_product_base;

    @ViewById(id = R.id.tv_empty_product_base_tip)
    private TextView tv_empty_product_base_tip;

    private ProductBaseAdapter productBaseAdapter;

    private ProductBaseService productBaseService;

    private static final  int QUERY_ALL_PRODUCT_MSG =136;

    private List<ProductBase> productBaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_base_list);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.product_list));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.create));
        lv_product_base.setEmptyView(tv_empty_product_base_tip);
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProductBaseListActivity.this,CreateProductBaseActivity.class);
                startActivity(intent);
            }
        });
        productBaseAdapter =new ProductBaseAdapter(this);
        productBaseService =new ProductBaseServiceImpl();

        lv_product_base.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=getIntent();
                intent.putExtra(PackSettingActivity.PRODUCT_BASE_ID, productBaseList.get(i).getId());
                setResult(PackSettingActivity.PRODUCT_BASE_RESULT,intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {

        showLoading();
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                productBaseList = productBaseService.queryAllProductBase();
                Message message=Message.obtain();
                message.what= QUERY_ALL_PRODUCT_MSG;
                message.obj= productBaseList;
                handler.sendMessage(message);

            }
        });

        super.onResume();
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_ALL_PRODUCT_MSG:
                    productBaseAdapter.setProductBaseList((List<ProductBase>) msg.obj);
                    lv_product_base.setAdapter(productBaseAdapter);
                    hideLoading();
                    break;
            }


            super.handleMessage(msg);
        }
    };
}
