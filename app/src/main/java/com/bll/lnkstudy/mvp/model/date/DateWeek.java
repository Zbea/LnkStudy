package com.bll.lnkstudy.mvp.model.date;

import java.io.Serializable;

public class DateWeek implements Serializable {

    public String name;
    public String identify;
    public int week;
    public boolean isCheck;
    public boolean isSelected;//该星期是否已选

    public DateWeek(String name, String identify, int week, boolean isCheck) {
        this.name = name;
        this.identify=identify;
        this.week = week;
        this.isCheck = isCheck;
    }
}
