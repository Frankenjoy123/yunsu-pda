package com.yunsoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunsoo.activity.R;
import com.yunsoo.activity.R.drawable;
import com.yunsoo.activity.R.id;
import com.yunsoo.activity.R.layout;
import com.yunsoo.unity.PackageDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Frank zhou on 7/15/2015.
 */
public class FileSyncAdapter extends BaseAdapter
{
    List<String> fileNames;
    LayoutInflater inflater;
    Activity activity;

   List<Integer> status;

    public FileSyncAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    @Override
    public int getCount() {
        return fileNames.size();
    }

    @Override
    public Object getItem(int i) {
        return fileNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.file_sync_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_pack_sync_file= (TextView) view.findViewById(id.tv_pack_sync_file);
            holder.iv_sync_status= (ImageView) view.findViewById(id.iv_sync_status);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();

        holder.tv_pack_sync_file.setText(fileNames.get(i));

        int drawableId;

        switch (status.get(i)){
            case 0:
                drawableId= drawable.icon_question_mark;
                break;
            case 1:
                drawableId=drawable.ic_synchronize_small;
                break;
            case 2:
                drawableId=drawable.ic_check;
                break;
            default:
                drawableId= drawable.icon_question_mark;
        }

        holder.iv_sync_status.setImageResource(drawableId);

        return view;
    }

    private final static class ViewHolder {
        TextView tv_pack_sync_file;
        ImageView iv_sync_status;
    }

}
