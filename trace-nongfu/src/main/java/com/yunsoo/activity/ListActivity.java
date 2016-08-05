package com.yunsoo.activity;

/**
 * Created by Frank Zhou  on 4/23/2015.
 */

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yunsoo.adapter.ViewAdapter;
import com.yunsoo.unity.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ListActivity extends Activity{
    private ListView lvDynamic;
	private Resources res;
	private ViewAdapter viewAdapter;
	private String ItemSize;
	private List<ListItem> listItems;
	private Context sharedPreferences;
	ArrayList<String> itemsTitleList;
	private int numPerGroup;
	private Button finishButton;
	private Intent intent;
	private ArrayList<String> deleteItemTitles;
	private Button btnRemoveAll;
	private boolean isClearAll=false;
	Builder builder;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d("ZXW", "ListActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getActionBar().hide();
        
        listItems=new ArrayList<ListItem>();
        deleteItemTitles=new ArrayList<String>();
        finishButton=(Button) findViewById(R.id.finish_btn);
        btnRemoveAll=(Button) findViewById(R.id.btnRemoveAll);
        finishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList list=new ArrayList();				
				list.add(deleteItemTitles);
				intent.putParcelableArrayListExtra("delete_titles", list);
				intent.putExtra("isClearAll", isClearAll);
				
				ListActivity.this.setResult(0, intent);
				Log.d("ZXW", "ListActivity setResult");
				finish();				
			}
		});
		
        
        btnRemoveAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                if(!listItems.isEmpty()){
                    builder=new Builder(ListActivity.this);

                    builder.setTitle("清空当前包内产品")
                            .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 自动生成的方法存根
                                    listItems.clear();
                                    viewAdapter.notifyDataSetChanged();
                                    isClearAll=true;
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();
                }
								
			}
		});

        intent=getIntent();
        /*
         * 方法如下:
			取的时候：
			ArrayList list = bundle.getParcelableArrayList("list");
			list2= (List<Object>) list.get(0);//强转成你自己定义的list，这样list2就是你传过来的那个list了。
         */
        
        ArrayList list=intent.getParcelableArrayListExtra("titles");
        itemsTitleList=(ArrayList<String>) list.get(0);
        //传入MainActivity的数据
        for (int i = 0; i < itemsTitleList.size(); i++) {
			Log.d("ZXW", String.valueOf(i));
			Log.d("ZXW", itemsTitleList.get(i));
			ListItem item=new ListItem();
			item.setTitle(itemsTitleList.get(i));
			listItems.add(item);
		} 
        try {
			Bundle bundle=intent.getExtras();
			numPerGroup=bundle.getInt("numPerGroup");
	        Log.d("ZXW", "每组数量"+numPerGroup);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
      InitializeListView();
      BindListViewEvent();
        
    }
	

	@Override
	protected void onStart() {
		// TODO 自动生成的方法存根
		super.onStart();
		Log.d("ZXW", "MainActivity onStart");
	}
	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		Log.d("ZXW", "MainActivity onResume");
	}


	private void InitializeListView() {
		// TODO 自动生成的方法存根
        try {
            lvDynamic = (ListView) findViewById(R.id.lvDynamic);
            
            res = this.getResources();
            viewAdapter = new ViewAdapter(this, res);
            viewAdapter.setTextIdList(listItems);
            lvDynamic.setAdapter(viewAdapter);
        }
        catch(Exception ex)
        {
//            logger.e(ex);
        	Log.d("ZXW", "ListActivity InitializeListView");
        }
	}
	
	private void BindListViewEvent(){
		lvDynamic.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int po=position;
				builder=new Builder(ListActivity.this);
				
				builder.setPositiveButton("删除",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteItemTitles.add(listItems.get(po).getTitle());
						listItems.remove(po);				
						viewAdapter.notifyDataSetChanged();		
					}
				})
				.create().show();
				
						
				return true;
			}
			
		});
	}

    private void ShowTotalNum()
    {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            int numS = (viewAdapter.getCount() / numPerGroup) + 1;
            int numM = (viewAdapter.getCount() % numPerGroup);

            TextView txt = (TextView) findViewById(R.id.textView);
            txt.setText(String.format("第%s组 第%s条 共%s条",numS,numM, String.valueOf(viewAdapter.getCount())));

            Button btnRemoveAll = (Button) findViewById(R.id.btnRemoveAll);
            if (viewAdapter.getCount() == 0) {

                btnRemoveAll.setEnabled(false);
            } else {

                btnRemoveAll.setEnabled(true);
            }
        }
        catch (Exception ex) {
//            logger.e(ex);
        	Log.d("ZXW", "ListActivity ShowTotalNum");
        }
    }

	private String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
	
	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();
		
		Log.d("ZXW", "ListActivity onPause");
	}
	
	
    
	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();
		Log.d("ZXW", "ListActivity onStop");
	}
	
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
				ArrayList list=new ArrayList();				
				list.add(deleteItemTitles);
				intent.putParcelableArrayListExtra("delete_titles", list);
				intent.putExtra("isClearAll", isClearAll);
				
				ListActivity.this.setResult(0, intent);
				Log.d("ZXW", "ListActivity setResult");
				finish();	
	  
	        }  
	          
	        return false;  
	          
	    }  
    	

}
   