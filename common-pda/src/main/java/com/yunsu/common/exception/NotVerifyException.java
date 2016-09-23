package com.yunsu.common.exception;

/**
 * Created by yunsu on 2016/8/31.
 */
public class NotVerifyException extends BaseException {

    public NotVerifyException( ) {
        super("扫码非氧泡泡官方认证");
    }

    public NotVerifyException(String msg ) {
        super(msg);
    }

}
