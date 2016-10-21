package com.yunsu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.greendao.entity.PatternInfo;

import java.util.List;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class KeyPatternAdapter extends BaseAdapter
{

    LayoutInflater inflater;
    Activity activity;

    List<PatternInfo> patternInfoList;

    public List<PatternInfo> getPatternInfoList() {
        return patternInfoList;
    }

    public void setPatternInfoList(List<PatternInfo> patternInfoList) {
        this.patternInfoList = patternInfoList;
    }

    public KeyPatternAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return patternInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return patternInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.key_pattern_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_example = (TextView) view.findViewById(id.tv_express);
            holder.tv_pattern_name = (TextView) view.findViewById(id.tv_pattern_name);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_example.setText(patternInfoList.get(i).getExample());
        holder.tv_pattern_name.setText(String.valueOf(patternInfoList.get(i).getName()));
        return view;
    }

    private final static class ViewHolder {
        TextView tv_pattern_name;
        TextView tv_example;
    }

}
