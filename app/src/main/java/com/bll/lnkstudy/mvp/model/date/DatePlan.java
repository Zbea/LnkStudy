package com.bll.lnkstudy.mvp.model.date;

import java.io.Serializable;

public class DatePlan implements Serializable {

    public int id;
    public String startTimeStr;//开始时间
    public boolean isStartSelect;
    public String endTimeStr;//结束时间
    public boolean isEndSelect;
    public String course;//科目
    public String content;

}
