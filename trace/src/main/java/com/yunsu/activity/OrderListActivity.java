package com.yunsu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yunsu.adapter.OrderAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.sqlite.service.MaterialService;
import com.yunsu.sqlite.service.impl.MaterialServiceImpl;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_order)
    PullToRefreshListView lv_order;

    @ViewById(id = R.id.tv_empty_order_tip)
    private TextView tv_empty_order_tip;

    public static final int ADD_NEW_ORDER_REQUEST = 123;

    public static final int ADD_NEW_ORDER_RESULT = 456;

    public static final int QUERY_ORDER_LIST_MSG = 167;

    public static final int ADD_ORDER_LIST_MSG=153;

    List<Material> materialList = new ArrayList<>();

    private MaterialService materialService;

    private OrderAdapter orderAdapter;

    public static final int PAGE_SIZE = 20;

    private static final int DELETE_MSG=101;
    private PackServiceImpl packService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        init();
    }

    private void init() {
        getActionBar().hide();
        titleBar.setTitle(getString(R.string.outbound_order));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.new_order));
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderListActivity.this, CreateOrderActivity.class);
                startActivityForResult(intent, ADD_NEW_ORDER_REQUEST);
            }
        });
        materialService=new MaterialServiceImpl();
        packService=new PackServiceImpl();
        orderAdapter = new OrderAdapter(this);
        orderAdapter.setMaterialList(materialList);
        lv_order.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv_order.setAdapter(orderAdapter);
        lv_order.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(OrderListActivity.this, OrderScanActivity.class);
                intent.putExtra(Constants.DB.ID, materialList.get(i-1).getId());
                startActivity(intent);
            }
        }));

        lv_order.setEmptyView(tv_empty_order_tip);
        lv_order.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                addEarlierList();
            }
        });
        lv_order.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int tempIndex=i-1;
                if (materialList.get(tempIndex).getProgressStatus()==Constants.DB.FINISHED
                        || materialList.get(tempIndex).getSent()== materialList.get(tempIndex).getAmount()
                        ){
                    AlertDialog dialog = new AlertDialog.Builder(OrderListActivity.this).setMessage(R.string.finished_order_can_not_delete)
                            .setNegativeButton(R.string.confirm, null).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }else {

                    AlertDialog dialog = new AlertDialog.Builder(OrderListActivity.this).setMessage(R.string.confirm_delete_order)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    ServiceExecutor.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            packService.batchDelete(materialService.queryPacks(materialList.get(tempIndex)));
                                            materialService.deleteMaterial(materialList.get(tempIndex));
                                            materialList.remove(tempIndex);
                                            handler.sendEmptyMessage(DELETE_MSG);
                                        }
                                    });

                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
                return false;
            }
        });
    }

    private void addEarlierList() {
        if (lv_order.getCurrentMode().equals(PullToRefreshBase.Mode.PULL_FROM_END)){
            ServiceExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    List<Material> resultList=materialService.queryMaterialByPage(materialList.size());
                    materialList.addAll(resultList);
                    handler.sendEmptyMessage(ADD_ORDER_LIST_MSG);
                }
            });
        }
    }


    @Override
    protected void onResume() {
        showLoading();
        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<Material> resultList = materialService.queryMaterialByPage(0);
                materialList.clear();
                materialList.addAll(resultList);
                handler.sendEmptyMessage(QUERY_ORDER_LIST_MSG);
            }
        });
        super.onResume();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case QUERY_ORDER_LIST_MSG:
                    hideLoading();
                    orderAdapter.notifyDataSetChanged();
                    break;

                case ADD_ORDER_LIST_MSG:
                    orderAdapter.notifyDataSetChanged();
                    lv_order.onRefreshComplete();
                    break;

                case DELETE_MSG:
                    orderAdapter.notifyDataSetChanged();
                    break;

            }
            super.handleMessage(msg);
        }
    };

}

