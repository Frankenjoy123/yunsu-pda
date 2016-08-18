package com.example.testapp;

import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.testapp.MainActivity;
import com.example.testapp.R;

/**
 * Created by yunsu on 2016/7/25.
 */
public class SampleTest extends InstrumentationTestCase {
    private MainActivity sample = null;
    private Button button = null;
    private TextView text = null;

    /*
     * 初始设置
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()  {
        try {
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClassName("com.example.testapp", MainActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sample = (MainActivity) getInstrumentation().startActivitySync(intent);
        text = (TextView) sample.findViewById(R.id.text1);
        button = (Button) sample.findViewById(R.id.button1);
    }

    /*
     * 垃圾清理与资源回收
     * @see android.test.InstrumentationTestCase#tearDown()
     */
    @Override
    protected void tearDown()  {
        sample.finish();
        try {
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 活动功能测试
     */
    public void testActivity() throws Exception {
        Log.v("testActivity", "test the Activity");
        SystemClock.sleep(1500);
        getInstrumentation().runOnMainSync(new PerformClick(button));
        SystemClock.sleep(3000);
        assertEquals("Hello Android", text.getText().toString());
    }

    /*
     * 模拟按钮点击的接口
     */
    private class PerformClick implements Runnable {
        Button btn;
        public PerformClick(Button button) {
            btn = button;
        }

        public void run() {
            btn.performClick();
        }
    }

    /*
     * 测试类中的方法
     */
    public void testAdd() throws Exception{
        String tag = "testAdd";
        Log.v(tag, "test the method");
        int test = sample.add(1, 1);
        assertEquals(2, test);
    }
}
