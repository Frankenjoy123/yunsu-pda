package com.yunsoo.sqlite;

import com.yunsoo.service.ServiceExecutor;

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
