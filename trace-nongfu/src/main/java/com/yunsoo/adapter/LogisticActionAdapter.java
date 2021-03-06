package com.yunsu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsu.activity.OrgAgencyActivity;
import com.yunsu.activity.OutBoundScanActivity;
import com.yunsu.activity.R.drawable;
import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.util.Constants;

import java.util.List;
import java.util.Map;

/**
 * Created by Frank zhou on 7/15/2015.
 */
public class LogisticActionAdapter extends BaseAdapter
{

    public static final String ACTION_ID="action_id";
    public static final String ACTION_NAME="action_name";
    List<Map<String, String>> actions;

    LayoutInflater inflater;
    Activity activity;
    private String actionId;
    private String actionName;

    public LogisticActionAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }
    public void setActions(List<Map<String, String>> actions) {
        this.actions = actions;
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public Object getItem(int i) {
        return actions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.action_item_layout_bottom,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_action= (TextView) view.findViewById(id.tv_action_name);
            holder.iv_action= (ImageView) view.findViewById(id.iv_image);
            holder.rl_action_item= (RelativeLayout) view.findViewById(id.rl_action_item);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();

        actionId=actions.get(i).keySet().iterator().next();
        actionName=actions.get(i).get(actionId);

        holder.tv_action.setText(actionName+"ic_inbound");

        switch (actionName){
            case Constants.Logistic.INBOUND:
                holder.iv_action.setImageResource(drawable.ic_box_in);
                break;
            case Constants.Logistic.OUTBOUND:
                holder.iv_action.setImageResource(drawable.ic_box_out);
                break;
        }

        final int finalPosition=i;
        holder.rl_action_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action_id=actions.get(finalPosition).keySet().iterator().next();
                String action_name=actions.get(finalPosition).get(action_id);
                Intent intent=null;
                if (action_id.equals(Constants.Logistic.INBOUND_CODE)){
                    intent=new Intent(activity, OutBoundScanActivity.class);
                }else if (action_id.equals(Constants.Logistic.OUTBOUND_CODE)){
                    intent=new Intent(activity, OrgAgencyActivity.class);
                }
                intent.putExtra(LogisticActionAdapter.ACTION_ID,action_id);
                intent.putExtra(LogisticActionAdapter.ACTION_NAME,action_name);
                activity.startActivity(intent);
            }
        });

        return view;
    }

    private final static class ViewHolder {
        TextView tv_action;
        ImageView iv_action;
        RelativeLayout rl_action_item;
    }

}
