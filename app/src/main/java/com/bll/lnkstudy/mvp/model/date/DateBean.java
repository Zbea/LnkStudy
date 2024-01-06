package com.bll.lnkstudy.mvp.model.date;

import com.bll.lnkstudy.utils.date.Lunar;
import com.bll.lnkstudy.utils.date.Solar;

import java.io.Serializable;
import java.util.List;

public class DateBean implements Serializable {

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
