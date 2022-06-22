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

public class StringUtils {

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

    /**
     * 处理空字符串
     */
    public static String isNull(Object obj) {
        String content = "";

        try {
            if (obj != null && !obj.toString().equals("") && !obj.toString().equals("null"))
                content = obj.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return content;
    }


    /**
     * 处理空字符串
     *
     * true 代表空  ||||   false 不为空
     */
    public static Boolean isNull_b(Object obj) {
        boolean content = true;

        try {
            if (obj != null && !obj.toString().equals("") && !obj.toString().equals("null") && obj.toString().length()>0)
                content = false;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return content;
    }

    /**
     * 处理空字符串
     */
    public static String isNulls(String obj) {
        String content = "--";
        if (obj != null && !obj.equals("") && !obj.equals("null"))
            content = obj.toString();
        return content;
    }

    public static String getProcessingResultsByCode(int code) {
        switch (code) {
            case 0:
                return "未处理";
            case 1:
                return "已处理";
            default:
                return "未知状态";
        }

    }

    /**
     * 带有*号的字符
     *
     * @param s      处理字符串
     * @param start  开始的下标
     * @param number *号的个数
     * @return
     */
    public static String StringShowStar(String s, int start, int number) {
        try {
            char[] chars = s.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                if (i >= start && i < start + number) {
                    builder.append("*");
                } else {
                    builder.append(chars[i]);
                }
            }
            return builder.toString();
        } catch (Exception e) {
            return e.getMessage();
        }

    }


    public static String getToDayTime() {
        String toDay = longToStringDataNoHour(System.currentTimeMillis());
        //String toDay ="2018-09-07";

        return toDay;

    }

    /**
     * 判断是否是11位的手机号
     * @param str
     * @return
     */
    public static boolean isPhoneNum(String str){
        String regex = "[1][3-9]\\d{9}";
        return str.matches(regex);
    }

    /**
     * 判断验证码 长度 4位 6位
     * @param str
     * @return
     */
    public static  boolean isVerifyCode(String str){
        String regex4 = "\\d{4}";
        String regex6 = "\\d{6}";
        return str.matches(regex4)||str.matches(regex6);
    }

    //是否是email
    public static boolean isEmailAddress(String phone) {
        String regex = "^^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 是否包含大小写字母及数字或
     * @param str
     * @param min  最低多少
     * @param max  最大多少
     * @return
     */
    public static boolean isLetterOrDigit(String str,int min,int max) {
        String regex = "^[a-zA-Z0-9]{"+min+","+max+"}$";
        return str.matches(regex);
    }

    /**
     * json解析是去掉回车空格换行 其他
     *    注：\n 回车(\u000a)
     *     \t 水平制表符(\u0009)
     *     \s 空格(\u0008)
     *     \r 换行(\u000d)
     *
     * @param str
     * @return 完整的string
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
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
