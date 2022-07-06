package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.greendao.DatePlanConverter;
import com.bll.lnkstudy.utils.greendao.DateRemindConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;

@Entity
public class DateEvent implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public int type;//0学习计划 1重要日子 2日程

    public String title;//标题
    public Long dayLong;//当天时间
    public String dayLongStr;
    public String explain;//说明

    public Long startTime;//开始时间
    public Long endTime;//结束时间
    public String startTimeStr;//开始时间
    public String endTimeStr;//结束时间

    @Convert(columnType = String.class,converter = DatePlanConverter.class)
    public List<DatePlanBean> list;//学习计划列表

    @Convert(columnType = String.class,converter = DateRemindConverter.class)
    public List<DateRemind> remindList;//提醒事件列表
    public String repeat;//重复类型

}
