package com.yunsoo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.activity.PathActivity;
import com.yunsoo.activity.R;
import com.yunsoo.activity.R.id;
import com.yunsoo.activity.R.layout;
import com.yunsoo.entity.OrgAgency;

import java.util.List;
import java.util.Map;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class ReportAdapter extends BaseAdapter
{


    List<OrgAgency> orgAgencyList;
    Map<String,OrgAgency> orgAgencyMap;
    LayoutInflater inflater;
    Activity activity;


    public ReportAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }

    public void setOrgAgencyList(List<OrgAgency> orgAgencyList) {
        this.orgAgencyList = orgAgencyList;
    }

    @Override
    public int getCount() {
        return orgAgencyList.size();
    }

    @Override
    public Object getItem(int i) {
        return orgAgencyList.get(i);
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
            holder.tv_report_agency_name = (TextView) view.findViewById(id.tv_report_agency_name);
            holder.tv_inbound_count= (TextView) view.findViewById(id.tv_inbound_count);
            holder.tv_outbound_count= (TextView) view.findViewById(id.tv_outbound_count);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_report_agency_name.setText(orgAgencyList.get(i).getName());
        holder.tv_inbound_count.setText(String.valueOf(orgAgencyList.get(i).getInbound_count())+"包");
        holder.tv_outbound_count.setText(String.valueOf(orgAgencyList.get(i).getOutbound_count())+"包");
        return view;
    }

    private final static class ViewHolder {
        TextView tv_report_agency_name;
        TextView tv_inbound_count;
        TextView tv_outbound_count;
    }

}
