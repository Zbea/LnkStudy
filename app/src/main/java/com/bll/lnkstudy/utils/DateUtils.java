package com.bll.lnkstudy.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by seatrend on 2018/8/21.
 */

public class DateUtils {

    /**
     * 基于当天剩余天数
     * @param date
     * @return
     */
    public static int sublongToDay(long date,long now){
        long daylong=24*60*60*1000;
        long sub=date-now;
        int day = 0;
        if (sub<daylong){
            day=0;
        }else {
            day= (int) (sub/daylong);
        }
        return day;
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToStringData(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToStringNoYear(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToString(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHMMSS", Locale.CHINA);
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToHour(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToHour1(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:m", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringWeek(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd HH:mm EEEE", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringDataNoHour(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringDataNoYear(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    //返回当前年月日
    String getNowDate()
    {
        Date date = new Date();
        String nowDate = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        return nowDate;
    }

    //返回当前年份
    public static int getYear()
    {
        Date date = new Date();
        String year = new SimpleDateFormat("yyyy").format(date);
        return Integer.parseInt(year);
    }

    //返回当前月份
    public static int getMonth()
    {
        Date date = new Date();
        String month = new SimpleDateFormat("MM").format(date);
        return Integer.parseInt(month);
    }
    //返回当前日
    public static int getDay()
    {
        Date date = new Date();
        String month = new SimpleDateFormat("dd").format(date);
        return Integer.parseInt(month);
    }


    /**
     *  把秒换算成 "yyyy-MM-dd"
     * @param date
     * @return
     */
    public static String intToStringDataNoHour(int date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date*1000L));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 整数返回星期几
     * @return
     */
    public static String intToWeek(int week){
        String weekStr="";
        if(week==1){
            weekStr="星期一";
        }
        else if (week==2){
            weekStr="星期二";
        }
        else if (week==3){
            weekStr="星期三";
        }
        else if (week==4){
            weekStr="星期四";
        }
        else if (week==5){
            weekStr="星期五";
        }
        else if (week==6){
            weekStr="星期六";
        }
        else {
            weekStr="星期天";
        }
        return weekStr;
    }

    public static long dateToStamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long date2Stamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long date3Stamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getToDayTime() {
        String toDay = longToStringDataNoHour(System.currentTimeMillis());
        //String toDay ="2018-09-07";

        return toDay;

    }


    /**
     *
     * 把时间long 的格式转为天
     * @return
     */
    public static String longToDay(long time){
        return String.valueOf(time/(24*60*60*1000));
    }

}
