package com.yunsoo.util;

import java.util.Calendar;

/**
 * Created by yunsu on 2016/8/10.
 */
public class DateUtil {

    public static String formatCalender(Calendar calendar){
        StringBuilder builder=new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR));
        builder.append("-");
        int month=calendar.get(Calendar.MONTH)+1;
        if (month<10){
            builder.append("0"+month);
        }else {
            builder.append(month);
        }
        builder.append("-");
        builder.append(calendar.get(Calendar.DAY_OF_MONTH));
        return builder.toString();
    }
}
