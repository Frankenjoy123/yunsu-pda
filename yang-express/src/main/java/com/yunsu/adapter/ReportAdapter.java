package com.yunsu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.entity.StaffCountEntity;

import java.util.List;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class ReportAdapter extends BaseAdapter
{

    List<StaffCountEntity> staffCountEntityList;
    LayoutInflater inflater;
    Activity activity;

    public ReportAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }

    public void setStaffCountEntityList(List<StaffCountEntity> staffCountEntityList) {
        this.staffCountEntityList = staffCountEntityList;
    }

    @Override
    public int getCount() {
        return staffCountEntityList.size();
    }

    @Override
    public Object getItem(int i) {
        return staffCountEntityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.report_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_staff_name = (TextView) view.findViewById(id.tv_staff_name);
            holder.tv_staff_pack_count = (TextView) view.findViewById(id.tv_staff_pack_count);
            holder.tv_staff_product_count = (TextView) view.findViewById(id.tv_staff_product_count);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_staff_name.setText(staffCountEntityList.get(i).getName());
        holder.tv_staff_pack_count.setText(String.valueOf(staffCountEntityList.get(i).getPackCount())+"箱");
        holder.tv_staff_product_count.setText(String.valueOf(staffCountEntityList.get(i).getProductCount())+"个");
        return view;
    }

    private final static class ViewHolder {
        TextView tv_staff_name;
        TextView tv_staff_pack_count;
        TextView tv_staff_product_count;
    }

}
