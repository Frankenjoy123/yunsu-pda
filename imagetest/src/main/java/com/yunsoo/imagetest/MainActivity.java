package com.yunsoo.imagetest;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {

    private ViewGroup.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logoLow= (ImageView) findViewById(R.id.iv_logoLow);
        ImageView logoHigh= (ImageView) findViewById(R.id.iv_logoHigh);


        RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(
                getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getWidth()/2);
        RelativeLayout.LayoutParams params2=new RelativeLayout.LayoutParams(
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getWidth()
                +getWindowManager().getDefaultDisplay().getWidth());
        logoLow.setLayoutParams(params1);
        logoHigh.setLayoutParams(params2);


    }

    public static RelativeLayout.LayoutParams getLayoutParams(Bitmap bitmap,int screenWidth){

        float rawWidth = bitmap.getWidth();
        float rawHeight = bitmap.getHeight();

        float width = 0;
        float height = 0;

        Log.i("hello", "原始图片高度：" + rawHeight + "原始图片宽度：" + rawWidth);
        Log.i("hello", "原始高宽比："+(rawHeight/rawWidth));

        if(rawWidth > screenWidth){
            height = (rawHeight/rawWidth)*screenWidth;
            width = screenWidth;
        }else{
            width = rawWidth;
            height = rawHeight;
        }

        Log.i("hello", "处理后图片高度："+height+"处理后图片宽度："+width);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)width, (int)height);

        return layoutParams;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
