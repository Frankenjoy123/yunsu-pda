package com.yunsoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.activity.PathActivity;
import com.yunsoo.activity.R;
import com.yunsoo.entity.OrgAgency;
import com.yunsu.common.util.Constants;

import java.util.List;

/**
 * Created by frank on 2016/6/20.
 */
public class OrgAgencySearchAdapter extends SearchAdapter<OrgAgency>{

    LayoutInflater inflater;
    private String actionId;
    private String actionName;

    public void setActionId(String actionId) {
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

                Intent intent = new Intent(context, PathActivity.class);
                intent.putExtra(LogisticActionAdapter.ACTION_ID,actionId);
                intent.putExtra(LogisticActionAdapter.ACTION_NAME,actionName);
                intent.putExtra(Constants.Logistic.AGENCY_ID,filteredContainer.get(i).getId());
                intent.putExtra(Constants.Logistic.AGENCY_NAME,filteredContainer.get(i).getName());
                context.startActivity(intent);
                Activity activity= (Activity) context;
                activity.finish();
            }
        });

        return view;
    }

    private final static class ViewHolder {
        TextView tv_org_agency;
        RelativeLayout rl_agency_item;
    }
}
