package com.tencent.tga.liveplugin.live.common.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lionljwang on 2016/6/20.
 */
public class TextUitl {
    public static String delSpace(String str) {
        if (str != null) {
            str = str.replaceAll("\r", "");
            str = str.replaceAll("\n", "");
        }
        return str;
    }

    public static String formatBigNum(long num, boolean isUseDecimal) {
        try {
            if (num >= 100000000) {
                return num / 100000000 + "亿";
            } else if (num >= 10000 && num < 100000000) {
                if (isUseDecimal) {
                    DecimalFormat df = new DecimalFormat("###.0");
                    return df.format(num / 10000) + "万";
                } else {
                    return num / 10000 + "万";
                }
            } else {
                return String.valueOf(num);
            }
        } catch (Exception e) {
            return "0";
        }

    }

    public static String addComma(String str) {
        try {
            if (Long.valueOf(str) >= 1000000000l) {
                double num = Double.valueOf(str) / 100000000l;
                DecimalFormat df = new DecimalFormat("#.00");
                return df.format(num) + "亿";
            }
            // 将传进数字反转
            String reverseStr = new StringBuilder(str).reverse().toString();
            String strTemp = "";
            for (int i = 0; i < reverseStr.length(); i++) {
                if (i * 3 + 3 > reverseStr.length()) {
                    strTemp += reverseStr.substring(i * 3, reverseStr.length());
                    break;
                }
                strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
            }
            // 将 【789,456,】 中最后一个【,】去除
            if (strTemp.endsWith(",")) {
                strTemp = strTemp.substring(0, strTemp.length() - 1);
            }
            // 将数字重新反转
            String resultStr = new StringBuilder(strTemp).reverse().toString();
            return resultStr;
        } catch (Exception e) {
            return str;
        }
    }
    private static String[] dayName = {"日","一","二","三","四","五","六"};

    public static String getTimeInterval(long currentTime, long desTime) {
        StringBuffer sb = new StringBuffer();
        long thisWeekEndTime = getEndDayOfWeek(new Date(currentTime)).getTime();
        long nextWeekEndTime = getEndDayOfNextWeek(new Date(currentTime)).getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(desTime);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int index = w > 0 ? w : 0;
        if (desTime <= getDayEndTime(new Date(currentTime)).getTime() && desTime >= getDayStartTime(new Date(currentTime)).getTime()) {
            sb.append("今天");
        } else if (desTime <= getTomorrowEndTime(new Date(currentTime)) && desTime > getDayEndTime(new Date(currentTime)).getTime()) {
            sb.append("明天");
        } else if (desTime > getTomorrowEndTime(new Date(currentTime)) && desTime <= getDayAfterTomorrowEndTime(new Date(currentTime))) {
            sb.append("后天");
        } else if (desTime > currentTime && desTime <= thisWeekEndTime) {
            sb.append("本周").append(dayName[index]);
        } else if (desTime > thisWeekEndTime && desTime <= nextWeekEndTime) {
            sb.append("下周").append(dayName[index]);
        } else {
            SimpleDateFormat sf = new SimpleDateFormat("M月dd日");
            sb.append(sf.format(desTime));
        }
        return sb.toString();
    }

    //获取本周的开始时间
    public static Date getBeginDayOfWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    //获取本周的结束时间
    public static Date getEndDayOfWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek(d));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    //获取下周的结束时间
    public static Date getEndDayOfNextWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek(d));
        cal.add(Calendar.DAY_OF_WEEK, 13);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    //获取今天的开始时间
    public static Date getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取某个日期的结束时间
    public static Date getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static long getTomorrowEndTime(Date d) {
        return getDayEndTime(d).getTime() + 24 * 60 * 60 *1000;
    }

    public static long getDayAfterTomorrowEndTime(Date d) {
        return getDayEndTime(d).getTime() + 2 * 24 * 60 * 60 *1000;
    }

}
