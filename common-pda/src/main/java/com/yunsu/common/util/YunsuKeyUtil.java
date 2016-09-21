package com.yunsu.common.util;

/**
 * Created by yunsu on 2016/8/31.
 */

import com.yunsu.common.exception.NotVerifyException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   verify the scan key
 */
public class YunsuKeyUtil {

    public static String verifyScanKey(String scanKey) throws NotVerifyException{
        Pattern pattern = Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
//        Pattern pattern=Pattern.compile("(.*)");
        Matcher matcher = pattern.matcher(scanKey);
        if (!matcher.find()) {
            throw new NotVerifyException();
        }
        return matcher.group(1);

    }


}
