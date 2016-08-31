package com.yunsu.common.util;

/**
 * Created by yunsu on 2016/8/31.
 */

import android.view.Gravity;
import android.widget.Toast;

import com.yunsu.common.exception.NotVerifyException;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   verify the scan key
 */
public class YunsuKeyUtil {

    public static String verifyScanKey(String scanKey) throws NotVerifyException {
        Pattern pattern = Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
        Matcher matcher = pattern.matcher(scanKey);
        if (!matcher.find()) {
            throw new NotVerifyException();
        }
        return matcher.group(1);
    }


}
