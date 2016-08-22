package com.yunsu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.entity.OrgAgency;
import com.yunsu.greendao.entity.Material;

import java.util.List;
import java.util.Map;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class OrderAdapter extends BaseAdapter
{

    List<Material> materialList;
    LayoutInflater inflater;
    Activity activity;

    public OrderAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }

    @Override
    public int getCount() {
        return materialList.size();
    }

    @Override
    public Object getItem(int i) {
        return materialList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.order_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_order_id = (TextView) view.findViewById(id.tv_order_id);
            holder.tv_agency_name= (TextView) view.findViewById(id.tv_agency_name);
            holder.tv_outbound_count= (TextView) view.findViewById(id.tv_outbound_count);
            holder.tv_outbound_amount= (TextView) view.findViewById(id.tv_outbound_amount);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_order_id.setText(String.valueOf(materialList.get(i).getId()));
        holder.tv_agency_name.setText(materialList.get(i).getAgencyName());
        holder.tv_outbound_count.setText(materialList.get(i).getSent()+"");
        holder.tv_outbound_amount.setText(materialList.get(i).getAmount()+"");
        return view;
    }

    private final static class ViewHolder {
        TextView tv_order_id;
        TextView tv_agency_name;
        TextView tv_outbound_count;
        TextView tv_outbound_amount;
    }

}
