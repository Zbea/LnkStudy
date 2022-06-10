package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

public class DatePlanBean implements Serializable {

    public int id;
    public long startTime;//开始时间
    public long endTime;//结束时间
    public String startTimeStr;//开始时间
    public String endTimeStr;//结束时间
    public String content;
    public boolean isOver=false;//判断是否填写完成

}
