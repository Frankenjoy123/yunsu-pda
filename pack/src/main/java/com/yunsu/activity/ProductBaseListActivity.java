package com.yunsu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.yunsu.adapter.ProductBaseAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.DensityUtil;
import com.yunsu.common.view.TitleBar;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeLeftRightMenuListView;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeMenu;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeMenuCreator;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeMenuItem;
import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.sqlite.service.ProductBaseService;
import com.yunsu.sqlite.service.impl.ProductBaseServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ProductBaseListActivity extends BaseActivity {

    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_product_base)
    SwipeLeftRightMenuListView lv_product_base;

    @ViewById(id = R.id.tv_empty_product_base_tip)
    private TextView tv_empty_product_base_tip;

    private ProductBaseAdapter productBaseAdapter;

    private ProductBaseService productBaseService;

    private static final  int QUERY_ALL_PRODUCT_MSG =136;

    private static final int DELETE_PRODUCT_MSG=201;

    public static final int CREATE_NEW_REQUEST=301;

    public static final int CREATE_NEW_RESULT=306;

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
                startActivityForResult(intent,CREATE_NEW_REQUEST);
            }
        });
        productBaseAdapter =new ProductBaseAdapter(this);
        productBaseList=new ArrayList<>();
        productBaseAdapter.setProductBaseList(productBaseList);
        lv_product_base.setAdapter(productBaseAdapter);

        productBaseService =new ProductBaseServiceImpl();

        lv_product_base.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=getIntent();
                intent.putExtra(Constants.PRODUCT_BASE_ID, productBaseList.get(i).getId());
                setResult(PackSettingActivity.PRODUCT_BASE_RESULT,intent);
                finish();
            }
        });

        initSwipeList();

    }


    private void initSwipeList() {

        SwipeMenuCreator creatorRight = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
//                openItem.setBackground();
                // set item background
//                <color name="red_btn_normal_color">#EC7272</color>
                openItem.setBackground(new ColorDrawable(Color.rgb(0xEC, 0x72, 0x72)));
//                openItem.setBackground(new ColorDrawable(R.color.red_btn_normal_color));
                // set item width
                openItem.setWidth(DensityUtil.dip2px(ProductBaseListActivity.this,100));
                // set item title
                openItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                openItem.setTitleSize(20);
                openItem.setId(1);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // // create "delete" item
                // SwipeMenuItem deleteItem = new
                // SwipeMenuItem(getApplicationContext());
                // // set item background
                // deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                // 0x3F, 0x25)));
                // // set item width
                // deleteItem.setWidth(dp2px(90));
                // // set a icon
                // deleteItem.setIcon(R.drawable.ic_delete);
                // // add to menu
                // menu.addMenuItem(deleteItem);

                menu.setViewType(1);
            }
        };

        // set creator
//        slt_collection.setLeftMenuCreator(creatorLeft);
        lv_product_base.setRightMenuCreator(creatorRight);

        // step 2. listener item click event
        lv_product_base.setOnMenuItemClickListener(new SwipeLeftRightMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {

                switch (menu.getViewType()) {
                    case 1:// right
                        if(productBaseList.size()>0){
                            ServiceExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    productBaseService.delete(productBaseList.get(position));
                                    productBaseList.remove(position);
                                    handler.sendEmptyMessage(DELETE_PRODUCT_MSG);
                                }
                            });
                        }

                        break;

                    default:
                        break;
                }

            }
        });

    }

    @Override
    protected void onResume() {

        showLoading();
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                productBaseList.clear();
                productBaseList.addAll(productBaseService.queryAllProductBase());
                handler.sendEmptyMessage(QUERY_ALL_PRODUCT_MSG);
            }
        });

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode== CREATE_NEW_REQUEST && resultCode==CREATE_NEW_RESULT){
            Intent intent=getIntent();
            intent.putExtra(Constants.PRODUCT_BASE_ID, data.getLongExtra(Constants.PRODUCT_BASE_ID,0));
            setResult(PackSettingActivity.PRODUCT_BASE_RESULT,intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_ALL_PRODUCT_MSG:
                    productBaseAdapter.notifyDataSetChanged();
                    hideLoading();
                    break;
                case DELETE_PRODUCT_MSG:
                    productBaseAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };
}
