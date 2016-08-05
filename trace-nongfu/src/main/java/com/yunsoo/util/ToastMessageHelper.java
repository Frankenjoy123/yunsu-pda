package com.yunsoo.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsoo.activity.R;


public class ToastMessageHelper {
	public static void showErrorMessage(Context context, String message, boolean shortDuration) {
		Toast.makeText(context, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
	}

	public static void showErrorMessage(Context context, int resId, boolean shortDuration) {
		Toast.makeText(context, resId, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
	}
	public static void showErrorMessage(Context context, int resId, int duration) {
		Toast.makeText(context, resId, duration).show();
	}

	public static void showMessage(Context context, int resId, boolean shortDuration) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView view = (TextView) inflater.inflate(R.layout.toast_layout, null);
		view.setText(resId);
		Toast toast = new Toast(context);
		toast.setView(view);
		toast.setDuration(shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
		toast.show();
	}
}
