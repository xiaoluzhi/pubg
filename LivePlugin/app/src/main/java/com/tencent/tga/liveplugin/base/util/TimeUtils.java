package com.tencent.tga.liveplugin.base.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hyqiao on 2016/11/7.
 */

public class TimeUtils {

    /**
    * 年月日、时分秒
    * @author hyqiao
    * @time 2016/11/7 17:04
    */
    public static String getCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(System.currentTimeMillis());
    }
    /**
     * 年月日、时分秒
     * @author hyqiao
     * @time 2016/11/7 17:04
     */
    public static String getCurrentTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(time);
    }
    /**
     * 年月日
     * @author hyqiao
     * @time 2016/11/7 17:04
     */
    public static String getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(System.currentTimeMillis());
    }

    public static String getMatchTime(long time){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(time);
        }catch (Exception e){
            return "";
        }
    }


    // 调用此方法输入所要转换的时间戳例如（1402733340000）输出（"06月14日"）
    public static String getMatchDate(long timeStamp){
        try {
            SimpleDateFormat sdr = new SimpleDateFormat("MM月dd日");
            return sdr.format(new Date(timeStamp));
        }catch (Exception e){
            return "";
        }
    }


    // 调用此方法输入所要转换的时间戳例如（1402733340000）输出（"06月14日 周一"）
    public static String getMatchDateWithWeek(long timeStamp){
        try {
            SimpleDateFormat sdr_month = new SimpleDateFormat("MM");
            SimpleDateFormat sdr_day = new SimpleDateFormat("dd");
            int month = Integer.valueOf(sdr_month.format(new Date(timeStamp)));
            int day = Integer.valueOf(sdr_day.format(new Date(timeStamp)));

            return String.format("%s月%s日 %s",month,day,getWeek(timeStamp));
        }catch (Exception e){
            return "";
        }
    }

    private static String getWeek(long timeStamp) {
        int mydate = 0;
        String week = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp));
        mydate = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;
    }


    /**
    * 输出时间格式  01:01   111：01
    * @author hyqiao
    * @time 2017/10/25 11:09
    */
    public static String getTimeFormatMinAndSec(int num){
        int minute = num/60;
        int second = num%60;
        return String.format("%s:%s",CalculateFormat(minute),CalculateFormat(second));
    }

    public static String CalculateFormat(int num){
        String result = "";
        if(num <= 0){
            result = "00";
        }else if(num<10){
            result = "0"+num;
        }else {
            result = ""+num;
        }
        return result;
    }
}
