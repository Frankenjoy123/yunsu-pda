package com.yunsu.adapter;

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

import com.yunsu.activity.PathActivity;
import com.yunsu.activity.R;
import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;

import java.util.List;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class OrgAgencyAdapter extends BaseAdapter
{


    List<String> orgAgencyList;

    LayoutInflater inflater;
    Activity activity;

    private int actionId;
    private String actionName;

    public OrgAgencyAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }

    public void setOrgAgencyList(List<String> orgAgencyList) {
        this.orgAgencyList = orgAgencyList;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
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
            view=inflater.inflate(layout.org_agency_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_org_agency = (TextView) view.findViewById(id.tv_org_agency);
            holder.rl_agency_item = (RelativeLayout) view.findViewById(id.rl_agency_item);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_org_agency.setText(orgAgencyList.get(i));
        holder.rl_agency_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder builder=new StringBuilder("确认选择");
                builder.append(orgAgencyList.get(i));
                builder.append("作为经销商吗？");
                AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.choose_org_agency).setMessage(builder.toString())
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(activity, PathActivity.class);
                                intent.putExtra(LogisticActionAdapter.ACTION_ID,actionId);
                                intent.putExtra(LogisticActionAdapter.ACTION_NAME,actionName);
                                activity.startActivity(intent);
                            }
                        }).setNegativeButton(R.string.no, null).create();
                dialog.setCancelable(false);
                dialog.show();

            }
        });

        return view;
    }

    private final static class ViewHolder {
        TextView tv_org_agency;
        RelativeLayout rl_agency_item;
    }

}
