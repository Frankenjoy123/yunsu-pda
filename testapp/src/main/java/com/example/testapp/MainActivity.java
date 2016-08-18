package com.example.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunsu.common.util.ToastMessageHelper;

public class MainActivity extends Activity {

    private TextView myText = null;
    private Button button = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myText = (TextView) findViewById(R.id.text1);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ToastMessageHelper.showErrorMessage(MainActivity.this,"我的哈哈哈",false);

                myText.setText("Hello Android");
            }
        });
    }

    public int add(int i, int j) {
        return (i + j);
    }
}
