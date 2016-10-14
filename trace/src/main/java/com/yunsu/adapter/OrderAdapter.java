package com.yunsu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.activity.R;
import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.common.util.Constants;
import com.yunsu.greendao.entity.Material;

import java.util.List;

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
            holder.tv_create_time= (TextView) view.findViewById(id.tv_create_time);
            holder.rl_order_item= (RelativeLayout) view.findViewById(id.rl_order_item);
            holder.tv_progress_status= (TextView) view.findViewById(id.tv_progress_status);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_order_id.setText(String.valueOf(materialList.get(i).getId()));
        holder.tv_agency_name.setText(materialList.get(i).getAgencyName());
        holder.tv_outbound_count.setText(materialList.get(i).getSent()+"");
        holder.tv_outbound_amount.setText(materialList.get(i).getAmount()+"");
        holder.tv_create_time.setText(materialList.get(i).getCreateTime());
        String progressStatus=null;
        int color = 0;
        switch (materialList.get(i).getProgressStatus()){
            case  Constants.DB.NOT_START:
                progressStatus=activity.getString(R.string.not_start);
                color=   activity.getResources().getColor(R.color.order_list_not_start);
                break;
            case Constants.DB.IN_PROGRESS:
                progressStatus=activity.getString(R.string.in_progress);
                color=activity.getResources().getColor(R.color.order_list_in_progress);
                break;
            case Constants.DB.FINISHED:
                progressStatus=activity.getString(R.string.finished);
                color=activity.getResources().getColor(R.color.order_list_finish);
                break;
            default:
                progressStatus=activity.getString(R.string.not_start);
                color=   activity.getResources().getColor(R.color.order_list_not_start);
                break;
        }
        holder.tv_progress_status.setText(progressStatus);
        holder.tv_progress_status.setTextColor(color);
        return view;
    }

    private final static class ViewHolder {
        TextView tv_order_id;
        TextView tv_agency_name;
        TextView tv_outbound_count;
        TextView tv_outbound_amount;
        TextView tv_progress_status;
        TextView tv_create_time;
        RelativeLayout rl_order_item;
    }

}
