package com.yunsu.common.util;

/**
 * Created by yunsu on 2016/8/31.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.yunsu.common.exception.NotVerifyException;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   verify the scan key
 */
public class YunsuKeyUtil {

    private static YunsuKeyUtil yunsuKeyUtil;

    private static Pattern productKeyPattern;

    private static Pattern packKeyPattern;

    private Context context;

    public YunsuKeyUtil(Context context) {
        this.context=context;
        SharedPreferences preferences=context.getSharedPreferences(Constants.PackPreference.PATTERN,Context.MODE_PRIVATE);

        String productString=preferences.getString(Constants.PackPreference.PRODUCT_PATTERN,"^(.*)$");

        String packString=preferences.getString(Constants.PackPreference.PACK_PATTERN,"^(.*)$");

    }

    public  String verifyProductKey(String productKey) throws NotVerifyException{
        Pattern pattern = Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
        Pattern pattern2 = Pattern.compile("^https?://ws.oyao.com/fw\\?f=(\\d+)$");
        Matcher matcher = pattern2.matcher(productKey);
        if (!matcher.find()) {
            throw new NotVerifyException();
        }
        return matcher.group(1);
    }

    public  String verifyPackageKey(String packKey) throws NotVerifyException{
//        Pattern pattern = Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
        Pattern pattern2 = Pattern.compile("^https?://ws.oyao.com/fw\\?f=(\\d+)$");
        Matcher matcher = pattern2.matcher(packKey);
        if (!matcher.find()) {
            throw new NotVerifyException();
        }
        return matcher.group(1);
    }



    public static YunsuKeyUtil initializeIntance(Context context) {

        if (yunsuKeyUtil == null) {
            yunsuKeyUtil = new YunsuKeyUtil(context);
        }
        return yunsuKeyUtil;
    }

    public static synchronized YunsuKeyUtil getInstance() {
        if (yunsuKeyUtil == null) {
            Logger logger=Logger.getLogger(YunsuKeyUtil.class);
            logger.error("yunsuKeyUtil has not been initialized");
        }
        return yunsuKeyUtil;
    }


    public static String verifyScanKey(String scanKey) throws NotVerifyException{
//        Pattern pattern = Pattern.compile("^https?:\\/\\/[\\w\\-\\.]+\\.yunsu\\.co(?:\\:\\d+)?(?:\\/p)?\\/([^\\/]+)\\/?$");
        Pattern pattern2 = Pattern.compile("^https?://ws.oyao.com/fw\\?f=(\\d+)$");
        Matcher matcher = pattern2.matcher(scanKey);
        if (!matcher.find()) {
            throw new NotVerifyException();
        }
        return matcher.group(1);
    }

    public static String verifyPackKey(String scanKey) throws NotVerifyException {
        Pattern pattern=Pattern.compile("^(\\d+)$");
        Matcher matcher=pattern.matcher(scanKey);
        if (!matcher.find()){
            throw  new NotVerifyException("包装码非条形码");
        }
        return matcher.group(1);
    }

}
