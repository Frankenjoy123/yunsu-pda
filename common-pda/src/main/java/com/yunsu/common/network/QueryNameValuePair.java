package com.yunsu.common.network;

import org.apache.http.NameValuePair;

/**
 * Created by yunsu on 2016/10/19.
 */
public class QueryNameValuePair implements NameValuePair {

    private String name;

    private String value;

    public QueryNameValuePair(String name , String value) {
        this.name=name;
        this.value=value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
