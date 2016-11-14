package com.yunsu.activity;

import android.content.Intent;

/**
 * Created by yunsu on 2016/11/11.
 */
public class AuthorizeActivityImpl extends com.yunsu.common.activity.AuthorizeActivity {
    @Override
    public void startTargetActivity() {
        Intent intent=new Intent(this,PathMainActivity.class);
        startActivity(intent);
    }
}
