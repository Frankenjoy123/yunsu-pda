package com.yunsoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.yunsoo.annotation.ViewById;
import com.yunsoo.service.ServiceExecutor;
import com.yunsoo.sqlite.service.MaterialService;
import com.yunsoo.sqlite.service.OrderService;
import com.yunsoo.sqlite.service.impl.MaterialServiceImpl;
import com.yunsoo.sqlite.service.impl.OrderServiceImpl;
import com.yunsoo.util.Constants;
import com.yunsoo.view.TitleBar;
import com.yunsu.greendao.entity.Material;
import com.yunsu.greendao.entity.Order;

import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class FillOrderNumberActivity extends BaseActivity {

    @ViewById(id = R.id.btn_query_order)
    private Button btn_query_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_order_number);

        init();

        btn_query_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                final Order order=new Order(null,"100","广州鲜果商店","李果","15622107965","天河路28号","自提","浙A-90876H","张送货","020-78965212","421087199010172719","江西农夫一号仓库",
                        Constants.DB.NOT_START,"2016-08-17");

                ServiceExecutor.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {

                        OrderService orderService=new OrderServiceImpl();
                        Order resultOrder=orderService.queryByOrderNumber("100");
                        if (resultOrder==null){
                            orderService.insertOrder(order);
                            resultOrder=orderService.queryByOrderNumber("100");
                            MaterialService materialService=new MaterialServiceImpl();

                            for(int i=1;i<=3;i++){
                                Material material=new Material(null,"800800600"+i,i*20+"",i+"","10","not_start",Constants.DB.NOT_START,(long)50,(long)0,(long)0,resultOrder.getId());
                                materialService.insertMaterial(material);
                            }
                        }
                        Message message=Message.obtain();
                        message.what=1;
                        handler.sendMessage(message);
                    }

                });

            }
        });
    }

    private void init() {
        getActionBar().hide();
        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(getString(R.string.fill_order_number));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);

    }

    private android.os.Handler handler=new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                hideLoading();
                Intent intent=new Intent(FillOrderNumberActivity.this,MaterialListActivity.class);
                startActivity(intent);
            }
            super.handleMessage(msg);
        }
    };

}
