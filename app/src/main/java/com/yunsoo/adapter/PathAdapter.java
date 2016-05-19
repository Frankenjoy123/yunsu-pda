package com.yunsoo.adapter;

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

import com.yunsoo.activity.R.drawable;
import com.yunsoo.activity.R.id;
import com.yunsoo.activity.R.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen Jerry on 3/9/2015.
 */
public class PathAdapter extends BaseAdapter
{
    private Context context;
    private List<String> keyList = new ArrayList<String>();
    
    public PathAdapter(Context context, Resources res)
    {
        this.context = context;
        this.res = res;

    }


	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
	}

	private Resources res;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(inflater);
            LinearLayout linearLayout = null;

            linearLayout = (LinearLayout) layoutInflater.inflate(
                    layout.list_item_path, null);

            TextView textviewNum = ((TextView) linearLayout
                    .findViewById(id.pathTextviewNum));

            String tmpText = "";
            if(position + 1 < 10)
                tmpText = "0" + String.valueOf(position + 1);
            else
                tmpText = String.valueOf(position + 1);

            textviewNum.setText(tmpText);

            TextView textView = ((TextView) linearLayout
                    .findViewById(id.pathTextview));
            textView.setText(keyList.get(position));

            ImageView imageView = ((ImageView) linearLayout
                    .findViewById(id.pathImage));
            imageView.setImageDrawable(res.getDrawable(drawable.package1));

            return linearLayout;
        }
        catch(Exception ex)
        {
        	Log.d("ZXW", "ViewAdapter getView");
            return null;
        }
    }



    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getCount()
    {
        return keyList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return keyList.get(position);
    }

}
