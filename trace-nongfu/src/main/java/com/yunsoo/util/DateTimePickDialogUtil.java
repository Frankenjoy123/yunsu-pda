package com.yunsu.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.yunsu.activity.R;
import com.yunsu.adapter.ReportAdapter;
import com.yunsu.entity.OrgAgency;
import com.yunsu.manager.LogisticManager;
import com.yunsu.service.ServiceExecutor;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yunsu on 2016/7/15.
 */
public class DateTimePickDialogUtil {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;

    public DateTimePickDialogUtil(Activity activity ) {
        this.activity = activity;
    }

    public AlertDialog dateTimePicKDialog(final TextView textView, final ListView listView, final TextView tv_in_count, final TextView tv_out_count) {
        final LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_date_time, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        init(datePicker,textView);

        ad = new AlertDialog.Builder(activity)
                .setTitle("选择日期")
                .setView(dateTimeLayout)
                .setPositiveButton("查询", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        StringBuilder builder=new StringBuilder();
                        String year= String.valueOf(datePicker.getYear());
                        int month=datePicker.getMonth()+1;
                        String monthString;
                        if (month<10){
                            monthString="0"+month;
                        }else {
                            monthString= String.valueOf(month);
                        }
                        int day=datePicker.getDayOfMonth();
                        String dayString;
                        if (day<10){
                            dayString="0"+day;
                        }else {
                            dayString= String.valueOf(day);
                        }

                        builder.append(year+"年");
                        builder.append(monthString+"月");
                        builder.append(dayString+"日");
                        textView.setText(builder.toString());
                        final String queryDate=year+"-"+monthString+"-"+dayString;
                        executeQueryReport(queryDate,activity,listView,tv_in_count,tv_out_count);
                    }
                })
                .setNegativeButton("取消", null).show();
        return ad;
    }

    public static void executeQueryReport(final String queryDate, final Activity activity, final ListView listView, final TextView tv_in_count, final TextView tv_out_count){

        ServiceExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                PackService packService=new PackServiceImpl();

                final Map<String , OrgAgency> agencyMap= packService.queryOrgAgentCount(queryDate);
                final Map<String, Integer> map = packService.queryInOutCount(queryDate);

                final List<OrgAgency> orgAgencyList=new ArrayList<OrgAgency>();
                //TODO
                List<OrgAgency> sourceList=LogisticManager.getInstance().getAgencies();
                for (Map.Entry<String, OrgAgency> entry:
                        agencyMap.entrySet()) {
                    String agencyId=  entry.getKey();
                    for(int i=0;i<sourceList.size();i++){
                        OrgAgency agency=sourceList.get(i);
                        if (agency.getId().equals(agencyId)){
                            entry.getValue().setName(agency.getName());
                        }
                    }
                    orgAgencyList.add(entry.getValue());
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int inCount=0,outCount=0;
                        if (map.get(Constants.Logistic.INBOUND_CODE)!=null){
                            inCount=map.get(Constants.Logistic.INBOUND_CODE);
                        }
                        if (map.get(Constants.Logistic.OUTBOUND_CODE)!=null){
                            outCount=map.get(Constants.Logistic.OUTBOUND_CODE);
                        }

                        tv_in_count.setText(inCount+"包");
                        tv_out_count.setText(outCount+"包");
                        ReportAdapter adapter=new ReportAdapter(activity);
                        adapter.setOrgAgencyList(orgAgencyList);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });

    }

    public void init(DatePicker datePicker, TextView tv_date) {
        Calendar mCalendar = Calendar.getInstance();
        datePicker.setMaxDate(mCalendar.getTimeInMillis());
        mCalendar.add(Calendar.MONTH,-3);
        datePicker.setMinDate(mCalendar.getTimeInMillis());

        Calendar calendar = getDateCalendar((String) tv_date.getText());
        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);


    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime
     *            初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String date = spliteString(initDateTime, "日", "index", "front"); // 日期
        String time = spliteString(initDateTime, "日", "index", "back"); // 时间

        String yearStr = spliteString(date, "年", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

        String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

        String hourStr = spliteString(time, ":", "index", "front"); // 时
        String minuteStr = spliteString(time, ":", "index", "back"); // 分

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

    private Calendar getDateCalendar(String date){

        Calendar calendar = Calendar.getInstance();
        String yearStr = spliteString(date, "年", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "年", "index", "back"); // 月日
        String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日
        dayStr=spliteString(dayStr,"日","index","front");
        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay);
        return calendar;
    }

    /**
     * 截取子串
     *
     * @param srcStr
     *            源串
     * @param pattern
     *            匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }
}
