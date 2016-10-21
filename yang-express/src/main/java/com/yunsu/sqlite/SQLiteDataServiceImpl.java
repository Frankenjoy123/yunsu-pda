package com.yunsu.sqlite;

import com.yunsu.common.service.ServiceExecutor;

/**
 * Created by Frank zhou on 2015/7/14.
 */
public class SQLiteDataServiceImpl implements Runnable{
    public void start() {
        ServiceExecutor.getInstance().execute(this);
    }
    @Override
    public void run() {

    }
}
