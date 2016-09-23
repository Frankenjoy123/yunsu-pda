package com.yunsu.common.util;

import com.yunsu.common.exception.NotVerifyException;

/**
 * Created by yunsu on 2016/9/23.
 */
public interface YunsuKeyAbstractUtil {
    String verifyKey(String key) throws NotVerifyException;
}
