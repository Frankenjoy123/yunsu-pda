package com.yunsu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsu.activity.R.id;
import com.yunsu.activity.R.layout;
import com.yunsu.greendao.entity.ProductBase;

import java.util.List;

/**
 * Created by Frank zhou on 6/20/2016.
 */
public class ProductBaseAdapter extends BaseAdapter
{

    LayoutInflater inflater;
    Activity activity;

    List<ProductBase> productBaseList;

    private long productBaseId;

    public long getProductBaseId() {
        return productBaseId;
    }

    public void setProductBaseId(long productBaseId) {
        this.productBaseId = productBaseId;
    }

    public List<ProductBase> getProductBaseList() {
        return productBaseList;
    }

    public void setProductBaseList(List<ProductBase> productBaseList) {
        this.productBaseList = productBaseList;
    }

    public ProductBaseAdapter(Activity activity) {
        this.activity=activity;
        this.inflater = activity.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return productBaseList.size();
    }

    @Override
    public Object getItem(int i) {
        return productBaseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(layout.product_item_layout,viewGroup,false);
            ViewHolder holder=new ViewHolder();
            holder.tv_product_name = (TextView) view.findViewById(id.tv_product_name);
            holder.tv_product_number = (TextView) view.findViewById(id.tv_product_number);
            holder.iv_check= (ImageView) view.findViewById(id.iv_check);
            view.setTag(holder);
        }
        ViewHolder holder= (ViewHolder) view.getTag();
        holder.tv_product_name.setText(productBaseList.get(i).getName());
        holder.tv_product_number.setText(String.valueOf(productBaseList.get(i).getProductNumber()));
        if (productBaseId==productBaseList.get(i).getId()){
            holder.iv_check.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private final static class ViewHolder {
        TextView tv_product_number;
        TextView tv_product_name;
        ImageView iv_check;
    }

}
