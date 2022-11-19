package com.bll.lnkstudy.utils.date;


public class Lunar {

    public boolean isleap;//是否闰月
    public int lunarDay;
    public int lunarMonth;
    public int lunarYear;
    public boolean isLFestival;
    public String lunarFestivalName;//农历节日

    final static String chineseNumber[] =
            {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};

    public String getChinaDayString(int day) {
        String chineseTen[] =
                {"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30||day==0)
            return "";
        if (day == 10)
            return "初十";
        else
            return chineseTen[day / 10] + chineseNumber[n];
    }
}
