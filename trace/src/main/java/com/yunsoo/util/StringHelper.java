package com.yunsoo.util;

import android.webkit.URLUtil;

import com.yunsoo.entity.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class StringHelper {

    public static boolean isStringNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static List<KeyValuePair> getKeyValuePairs(String s) {
        if (StringHelper.isStringNullOrEmpty(s)) {
            return new ArrayList<KeyValuePair>();
        }

        List<KeyValuePair> pairs = new ArrayList<KeyValuePair>();

        String replacedDetails = s.replaceAll("", "");
        String[] items = replacedDetails.split(";");
        if (items != null && items.length > 0) {
            for (String item : items) {
                String[] keyValue = item.split(":");
                if (keyValue != null && keyValue.length == 2) {
                    pairs.add(new KeyValuePair(keyValue[0], keyValue[1]));
                }
            }
        }
        return pairs;
    }

    public static int[] getLastThreeSplashPos(String sUrl) {
        String sGetUrl = sUrl;
        int arIdx[] = new int[3];
        for (int i = 0; i < 3; i++) {
            arIdx[i] = sGetUrl.lastIndexOf("/");
            sGetUrl = sGetUrl.substring(0, arIdx[i]);
        }
        return arIdx;
    }

    public static int parseStringToInt(String sContent, int iDefaultValue) {
        try {
            int iRes = Integer.parseInt(sContent);
            return iRes;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return iDefaultValue;
        }
    }

    public static int getSortIDFromUrl(String sUrl, int iDefaultValue) {
        int arIdx[] = getLastThreeSplashPos(sUrl);
        return parseStringToInt(sUrl.substring(arIdx[2] + 1, arIdx[1]), iDefaultValue);
    }

    public static String[] toStringArray(JSONArray array) {
        int length = array.length();
        String[] strArray = new String[length];
        try {

            for (int i = 0; i < length; i++) {
                strArray[i] = array.getString(i);
            }
        } catch (JSONException e) {
            // TODO: handle exception
        }

        return strArray;
    }

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }


    public static String join(String seperator, String[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {
                sb.append(array[i]);
                if (i < array.length - 1) {
                    sb.append(seperator);
                }
            }

        }
        return sb.toString();

    }

    public static boolean isHttpUrl(String text) {
        return URLUtil.isHttpUrl(text) || URLUtil.isHttpsUrl(text);
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^\\d{11}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
