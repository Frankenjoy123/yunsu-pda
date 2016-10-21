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

import com.yunsu.adapter.StaffAdapter;
import com.yunsu.common.annotation.ViewById;
import com.yunsu.common.service.ServiceExecutor;
import com.yunsu.common.util.Constants;
import com.yunsu.common.util.DensityUtil;
import com.yunsu.common.util.ToastMessageHelper;
import com.yunsu.common.view.TitleBar;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeLeftRightMenuListView;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeMenu;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeMenuCreator;
import com.yunsu.common.view.swipeleftrightmenulistview.SwipeMenuItem;
import com.yunsu.greendao.entity.Staff;
import com.yunsu.sqlite.service.StaffService;
import com.yunsu.sqlite.service.impl.StaffServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class StaffListActivity extends BaseActivity {
    @ViewById(id = R.id.title_bar)
    TitleBar titleBar;

    @ViewById(id = R.id.lv_staff)
    SwipeLeftRightMenuListView lv_staff;

    @ViewById(id = R.id.tv_empty_staff_tip)
    private TextView tv_empty_staff_tip;

    private StaffAdapter staffAdapter;

    private StaffService staffService;

    private static final  int QUERY_ALL_STAFF_MSG=134;

    private static final int DELETE_STAFF_MSG=104;

    private static final int EXIST_PACK_DATA_MSG= 106;

    public static final int CREATE_NEW_STAFF_REQUEST=205;

    public static final int CREATE_NEW_STAFF_RESULT=207;

    private List<Staff> staffList;

    private long staffId;

    private static final long NULL_STAFF_ID=-2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);
        init();
    }

    private void init() {
        getActionBar().hide();

        staffId=getIntent().getLongExtra(PackSettingActivity.STAFF_ID,0);

        staffList=new ArrayList<>();

        titleBar.setTitle(getString(R.string.staff_list));
        titleBar.setDisplayAsBack(true);
        titleBar.setMode(TitleBar.TitleBarMode.BOTH_BUTTONS);
        titleBar.setRightButtonText(getString(R.string.create));
        lv_staff.setEmptyView(tv_empty_staff_tip);
        titleBar.setOnRightButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StaffListActivity.this,CreateStaffActivity.class);
                startActivityForResult(intent,CREATE_NEW_STAFF_REQUEST);
            }
        });
        staffAdapter=new StaffAdapter(this);
        staffAdapter.setStaffList(staffList);
        staffAdapter.setStaffId(staffId);

        lv_staff.setAdapter(staffAdapter);

        staffService=new StaffServiceImpl();

        lv_staff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=getIntent();
                intent.putExtra(PackSettingActivity.STAFF_ID,staffList.get(i).getId());
                setResult(PackSettingActivity.STAFF_RESULT,intent);
                finish();
            }
        });

        initSwipeList();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==CREATE_NEW_STAFF_REQUEST && resultCode==CREATE_NEW_STAFF_RESULT){
            Intent intent=getIntent();
            intent.putExtra(PackSettingActivity.STAFF_ID,data.getLongExtra(Constants.STAFF_ID,0));
            setResult(PackSettingActivity.STAFF_RESULT,intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                openItem.setWidth(DensityUtil.dip2px(StaffListActivity.this,100));
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
        lv_staff.setRightMenuCreator(creatorRight);

        // step 2. listener item click event
        lv_staff.setOnMenuItemClickListener(new SwipeLeftRightMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {

                switch (menu.getViewType()) {
                    case 1:// right
                        if(staffList.size()>0){
                            if (staffList.get(position).getId()==1){
                                ToastMessageHelper.showMessage(StaffListActivity.this,R.string.can_not_delete_default_staff,true);
                            }else {
                                ServiceExecutor.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean existPackData = staffService.existPackDataByStaffId(staffList.get(position).getId());
                                        if (existPackData){
                                            handler.sendEmptyMessage(EXIST_PACK_DATA_MSG);
                                        }else {
                                            Message message=Message.obtain();
                                            message.what=DELETE_STAFF_MSG;
                                            message.obj=staffList.get(position).getId();
                                            staffService.delete(staffList.get(position));
                                            staffList.remove(position);
                                            handler.sendMessage(message);
                                        }
                                    }
                                });
                            }


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
                staffList.clear();
                staffList.addAll(staffService.queryAllStaff());
                handler.sendEmptyMessage(QUERY_ALL_STAFF_MSG);

            }
        });

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra(PackSettingActivity.STAFF_ID,staffId);
        setResult(PackSettingActivity.STAFF_RESULT,intent);
        super.onBackPressed();
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_ALL_STAFF_MSG:
                    staffAdapter.notifyDataSetChanged();
                    hideLoading();
                    break;
                case DELETE_STAFF_MSG:
                    staffAdapter.notifyDataSetChanged();
                    if (staffId!=0 && staffId == (long)msg.obj){
                        staffId=NULL_STAFF_ID;
                    }
                    break;
                case EXIST_PACK_DATA_MSG:
                    ToastMessageHelper.showMessage(StaffListActivity.this,R.string.exist_pack_data,true);
                    break;
                default:
                    break;
            }


            super.handleMessage(msg);
        }
    };

}
