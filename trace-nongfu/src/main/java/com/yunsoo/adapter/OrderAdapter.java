package com.yunsoo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.activity.OutBoundScanActivity;
import com.yunsoo.activity.R;
import com.yunsoo.entity.MaterialEntity;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends BaseAdapter {

	LayoutInflater inflater;

	List<MaterialEntity> materialEntityList = new ArrayList<MaterialEntity>();

	Activity activity;

	public OrderAdapter(Activity activity) {
		this.activity=activity;
		inflater = activity.getLayoutInflater();
	}

	public void setMaterialEntityList(List<MaterialEntity> materialEntityList) {
		if (materialEntityList==null||materialEntityList.size()==0){
			return;
		}
		this.materialEntityList.clear();
		this.materialEntityList.addAll(materialEntityList);

		this.materialEntityList = materialEntityList;
	}

	@Override
	public int getCount() {
		return materialEntityList.size();
	}

	@Override
	public Object getItem(int position) {

		return materialEntityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MaterialEntity item = (MaterialEntity) getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.order_item_layout,
					parent, false);
			ViewHolder holder = new ViewHolder();

			holder.tv_material_number = (TextView) convertView
					.findViewById(R.id.tv_material_number);
			holder.tv_amount = (TextView) convertView
					.findViewById(R.id.tv_amount);
			holder.tv_product_name = (TextView) convertView
					.findViewById(R.id.tv_product_name);
			holder.rl_order_item= (RelativeLayout) convertView.findViewById(R.id.rl_order_item);
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();

		holder.tv_material_number.setText(item.getMaterialNumber());
		holder.tv_amount.setText(String.valueOf(item.getAmount()));
		holder.tv_product_name.setText(item.getProductName());
		holder.rl_order_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(activity,OutBoundScanActivity.class);
				Bundle bundle=new Bundle();
				bundle.putParcelable("Material",item);
				intent.putExtra("bundle",bundle);
				activity.startActivity(intent);
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		public TextView tv_material_number;
		public TextView tv_amount;
		public TextView tv_product_name;
		public RelativeLayout rl_order_item;
	}

}
