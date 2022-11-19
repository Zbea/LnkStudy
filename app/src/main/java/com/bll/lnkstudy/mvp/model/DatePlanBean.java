package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

public class DatePlanBean implements Serializable {

    public int id;
    public long startTime;//开始时间
    public long endTime;//结束时间
    public String startTimeStr;//开始时间
    public String endTimeStr;//结束时间
    public String course;//科目
    public String content;
    public boolean isRemindStart;
    public boolean isRemindEnd;

}
