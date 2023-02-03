package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

public class DateWeek implements Serializable {

    public String name;
    public String identify;
    public int week;
    public boolean isCheck;

    public DateWeek(String name, String identify, int week, boolean isCheck) {
        this.name = name;
        this.identify=identify;
        this.week = week;
        this.isCheck = isCheck;
    }
}
