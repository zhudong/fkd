package com.fuexpress.kr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Longer on 2016/10/26.
 */
public class TimeUtils {
    public static String getTimeMethod(long timeMillis) {
        Date dt = new Date();
        long l = timeMillis * 1000;
        Long time = dt.getTime();
        //LogUtils.e("这是目前时间:" + time);
        long nowTime = time / 1000;
        long totalSeconds = nowTime - timeMillis;
        //LogUtils.e("这是现在的秒值:" + nowTime);
        //LogUtils.e("这是传进来的秒值:" + timeMillis);
        //LogUtils.e("这是当前时间减去初始时间:" + totalSeconds);

        long day1 = totalSeconds / (24 * 3600);
        long hour1 = totalSeconds % (24 * 3600) / 3600;
        long minute1 = totalSeconds % 3600 / 60;
        long second1 = totalSeconds % 60 / 60;

        Date currYearFirst = getCurrYearFirst();

        long time1 = currYearFirst.getTime();

        long timesmorning = getTimesmorning();
        //LogUtils.e("这是今天的0点:" + timesmorning);
        //SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //String format1 = df1.format(timesmorning);
        //LogUtils.e("这是今天0点的转换:" + format1);

        if (day1 <= 0) {
            //当日日期小于1天时:
            if (hour1 <= 0) {
                //当时小于等于0时:
                if (minute1 <= 1) {
                    return "刚刚";
                } else if (minute1 > 1) {
                    return minute1 + "分钟前";
                }
            } else if (hour1 >= 1 && hour1 < 24) {
                return hour1 + "小时以前";
            }
        } else if (day1 > 1 && l > time1) {
            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
            String format = df.format(l);
            return format;
        } else if (day1 >= 1 && day1 < 2) {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String format = df.format(l);
            return "昨天" + format;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(l);
        return date;

    }

    public static String getDateStyleString(long time) {
        long l = time * 1000;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(l);
        return date;
    }

    public static String getDateStyle(long time) {
        long l = time * 1000;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(l);
        return date;
    }

    public static String getDateStyleStringByLong(long time) {
        long l = time;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(l);
        return date;
    }

    public static String getDateStyleStringEndHour() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        String format = dateFormat.format(System.currentTimeMillis());
        return format;
    }


    /**
     * 获取当年的第一天
     *
     * @param
     * @return
     */
    public static Date getCurrYearFirst() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某一天的24点:
     */

    public static long getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }


    //获得当天0点时间
    public static long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取今天0点开始的秒数
     *
     * @return long
     */
    public static long getTimeNumberToday() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = yyyyMMdd.format(date);
        try {
            date = yyyyMMdd.parse(str);
            return date.getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long getTodayTimeLongValue() {
        Date dt = new Date();
        Long time = dt.getTime();
        return time / 1000L;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate() {
        Date dt = new Date();
        String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}