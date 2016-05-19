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

import com.yunsoo.unity.ListItem;
import com.yunsoo.activity.R.drawable;
import com.yunsoo.activity.R.id;
import com.yunsoo.activity.R.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen Jerry on 3/9/2015.
 */
public class ViewAdapter extends BaseAdapter
{
    private Context context;
    private List<ListItem> textIdList = new ArrayList<ListItem>();
    
    public void setTextIdList(List<ListItem> textIdList) {
		this.textIdList = textIdList;
	}

	private Resources res;
   // private MyLog logger = MyLog.yLog();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(inflater);
            LinearLayout linearLayout = null;

            linearLayout = (LinearLayout) layoutInflater.inflate(
                    layout.list_item_product, null);

            TextView textviewNum = ((TextView) linearLayout
                    .findViewById(id.textviewNum));

            String tmpText = "";
            if(position + 1 < 10)
                tmpText = "0" + String.valueOf(position + 1);
            else
                tmpText = String.valueOf(position + 1);

            textviewNum.setText(tmpText);

            TextView textView = ((TextView) linearLayout
                    .findViewById(id.textview));
            textView.setText(textIdList.get(position).getTitle());

            ImageView imageView = ((ImageView) linearLayout
                    .findViewById(id.image));
            imageView.setImageDrawable(res.getDrawable(drawable.product));

            return linearLayout;
        }
        catch(Exception ex)
        {
   //         logger.e(ex);
        	Log.d("ZXW", "ViewAdapter getView");
            return null;
        }
    }

    public ViewAdapter(Context context, Resources res)
    {
        this.context = context;
        this.res = res;

    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getCount()
    {
        return textIdList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return textIdList.get(position);
    }

    public List<ListItem> getTextIdList()
    {
        return textIdList;
    }

    public void setAsProductText(int index) {
        try {
            if (index < 0)
                return;
            ListItem item = textIdList.get(index);
            item.setImage(res.getDrawable(drawable.product));
            item.setPackage(false);
            notifyDataSetChanged();
        }
        catch(Exception ex)
        {
      //      logger.e(ex);
        }
    }

    public void addText(String text) {
        try {
            ListItem item = new ListItem();
            item.setPackage(false);
            item.setTitle(text);
            item.setImage(res.getDrawable(drawable.product));
            textIdList.add(item);
            notifyDataSetChanged();
        }
        catch(Exception ex)
        {
          //  logger.e(ex);
        }
    }
    
    


    public void remove(int index)
    {
        try {
            if (index < 0)
                return;
            textIdList.remove(index);
            notifyDataSetChanged();
        }
        catch(Exception ex)
        {
          //  logger.e(ex);
        }
    }

    public void removeAll()
    {
        try {
            textIdList.clear();
            notifyDataSetChanged();
        }
        catch(Exception ex)
        {
        //    logger.e(ex);
        }
    }

}
