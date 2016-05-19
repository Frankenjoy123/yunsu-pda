package com.yunsoo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    
	public static String getLastString(String str) {
        String[] splitStr = str.split("/");
        int len = splitStr.length;

        if(len == 0)
            return str;

        String result = splitStr[len - 1];
        return result;
    }

}
