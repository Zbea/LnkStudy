package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.date.Lunar;
import com.bll.lnkstudy.utils.date.Solar;

public class DateBean {

    public int year;
    public int month;
    public int day;
    public int week;
    public long time;
    public boolean isNow;//是否是当天
    public boolean isNowMonth;//是否是当月

    public Solar solar=new Solar();
    public Lunar lunar=new Lunar();

}
