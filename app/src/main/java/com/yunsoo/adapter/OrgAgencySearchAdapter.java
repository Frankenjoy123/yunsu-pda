package com.yunsu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.activity.PathActivity;
import com.yunsu.activity.R;
import com.yunsu.entity.OrgAgency;

import java.util.List;

/**
 * Created by frank on 2016/6/20.
 */
public class OrgAgencySearchAdapter extends SearchAdapter<OrgAgency>{

    LayoutInflater inflater;
    private int actionId;
    private String actionName;

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public OrgAgencySearchAdapter(List<OrgAgency> container, Context context) {
        super(container, context);
        inflater=((Activity)context).getLayoutInflater();
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(R.layout.org_agency_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_org_agency = (TextView) view.findViewById(R.id.tv_org_agency);
            holder.rl_agency_item = (RelativeLayout) view.findViewById(R.id.rl_agency_item);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_org_agency.setText(filteredContainer.get(i).getName());
        holder.rl_agency_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder builder=new StringBuilder("确认选择");
                builder.append(filteredContainer.get(i).getName());
                builder.append("作为经销商吗？");
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle(R.string.choose_org_agency).setMessage(builder.toString())
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, PathActivity.class);
                                intent.putExtra(LogisticActionAdapter.ACTION_ID,actionId);
                                intent.putExtra(LogisticActionAdapter.ACTION_NAME,actionName);
                                context.startActivity(intent);
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
