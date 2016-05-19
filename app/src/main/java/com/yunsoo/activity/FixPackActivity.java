package com.yunsoo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsoo.adapter.ProductInPackageAdapter;
import com.yunsoo.fileOpreation.PackDetailFileRead;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.sqlite.SQLiteOperation;
import com.yunsoo.util.Constants;
import com.yunsoo.util.StringUtils;
import com.yunsoo.view.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FixPackActivity extends Activity {
    private TitleBar titleBar;
    private TextView tv_fix_barcode;

    private String packCode;
    private List<String> productCodes;
    private List<String> originalCodes;
    private ProductInPackageAdapter adapter;
    private ListView lv_fix_products;
    private EditText et_get_packCode;
    private EditText et_get_productCode;
    private AlertDialog.Builder builder;

    private TextView tv_fix_tip;

    private LinearLayout ll_fix_top;
    private int originalSize;


    private MyDataBaseHelper dataBaseHelper;
    private TextView tv_fix_pack_code;

    public static final String QUERY_TASK="query";
    public static final String UPDATE_TASK="update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_pack);
//        correctString="";
        getActionBar().hide();
        dataBaseHelper=new MyDataBaseHelper(this, Constants.SQ_DATABASE,null,1);
        titleBar=(TitleBar) findViewById(R.id.fix_title_bar);
        ll_fix_top= (LinearLayout) findViewById(R.id.ll_fix_top);
        ll_fix_top.setVisibility(View.INVISIBLE);

        tv_fix_tip= (TextView) findViewById(R.id.tv_fix_tip);

        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setDisplayAsBack(true);
        titleBar.setTitle("修改包装");
        titleBar.setRightButtonText("完成");
        try {
            titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(originalSize>0){

                        StringBuilder builder1=new StringBuilder();

                        for (int i=0;i<productCodes.size();i++){
                            builder1.append(productCodes.get(i));
                            if (i<productCodes.size()-1){
                                builder1.append(",");
                            }
                        }

                        MyAsyncTask task=new MyAsyncTask(FixPackActivity.this);
                        task.execute(UPDATE_TASK,builder1.toString(),packCode);

                    }

                }
            });

        } catch (Exception e) {
            finish();
            e.printStackTrace();
        }

        productCodes=new ArrayList<String>();
        originalCodes=new ArrayList<String>();
        adapter=new ProductInPackageAdapter(FixPackActivity.this,getResources());
        adapter.setProductIdList(productCodes);
        lv_fix_products= (ListView) findViewById(R.id.lv_fix_products);
        lv_fix_products.setAdapter(adapter);
        tv_fix_pack_code= (TextView) findViewById(R.id.tv_right_pack_barcode);
        et_get_packCode= (EditText) findViewById(R.id.et_get_packCode);
        et_get_productCode= (EditText) findViewById(R.id.et_get_productCode);
        et_get_packCode.requestFocus();
        bindPackBarcodeChanged();
        bindProductBarcodeChanged();
        bindListViewEvent();
    }


    private void bindListViewEvent(){
        lv_fix_products.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                final int po=position;
                builder=new AlertDialog.Builder(FixPackActivity.this);

                builder.setPositiveButton("删除",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        productCodes.remove(po);
                        adapter.notifyDataSetChanged();

                    }
                })
                 .create().show();


                return true;
            }

        });
    }

    private void bindProductBarcodeChanged() {
        et_get_productCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(productCodes.size()<originalSize){
                    String string = new StringBuilder(s).toString();
                    string=StringUtils.getLastString(StringUtils.replaceBlank(string));
                    productCodes.add(string);

                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "该包已装满，请按完成", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER , 0, 0);
                    toast.show();
                }

            }
        });
    }

    private void bindPackBarcodeChanged() {
        et_get_packCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = new StringBuilder(s).toString();
                string=StringUtils.getLastString(StringUtils.replaceBlank(string));
                productCodes.clear();

                MyAsyncTask task=new MyAsyncTask(FixPackActivity.this);
                task.execute(QUERY_TASK,string);

            }
        });
    }

    private class MyAsyncTask extends AsyncTask<String,Integer,String> {

        Context context;
        MyAsyncTask(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(final String... params) {

            try {
                switch (params[0]){
                    case UPDATE_TASK:
                        dataBaseHelper.getWritableDatabase().execSQL("update pack set product_keys=? where pack_key=?"
                                ,new String[]{params[1],params[2]});

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();

                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getString(R.string.fix_finish), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM,0,0);
                                toast.show();

                            }
                        });
                        break;
                    case QUERY_TASK:
                        final Cursor cursor=dataBaseHelper.getReadableDatabase().rawQuery("select * from pack where pack_key=?",
                                new String[]{params[1]});
                        if (cursor!=null&&cursor.getCount()>0){
                            packCode=params[1];
                            while (cursor.moveToNext()){
                                String products=cursor.getString(2);
                                String[] arrayStrings=products.split(",");

                                for(int j=0;j<arrayStrings.length;j++){
                                    productCodes.add(arrayStrings[j]);
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(productCodes.size()>0){
                                    tv_fix_pack_code.setText(packCode);
                                    ll_fix_top.setVisibility(View.VISIBLE);
                                    tv_fix_tip.setVisibility(View.INVISIBLE);

                                    et_get_packCode.clearFocus();
                                    et_get_productCode.requestFocus();

                                    originalCodes.addAll(productCodes);
                                    originalSize=productCodes.size();
                                    adapter.notifyDataSetChanged();

                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "找不到该包装码", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER , 0, 100);
                                    toast.show();
                                }
                            }
                        });


                }


            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onStop() {
        dataBaseHelper.close();
        super.onStop();
    }
}
