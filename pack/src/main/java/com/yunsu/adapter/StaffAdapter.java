package com.yunsu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.greendao.entity.Staff;

import java.util.List;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class StaffAdapter extends BaseAdapter
{

    LayoutInflater inflater;
    Activity activity;

    List<Staff> staffList;

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    public StaffAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return staffList.size();
    }

    @Override
    public Object getItem(int i) {
        return staffList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.staff_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_staff_name = (TextView) view.findViewById(id.tv_staff_name);
            holder.tv_staff_number= (TextView) view.findViewById(id.tv_staff_number);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_staff_name.setText(staffList.get(i).getName());
        holder.tv_staff_number.setText(String.valueOf(staffList.get(i).getStaffNumber()));
        return view;
    }

    private final static class ViewHolder {
        TextView tv_staff_number;
        TextView tv_staff_name;
    }

}
