package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.greendao.DatePlanConverter;
import com.bll.lnkstudy.utils.greendao.DateRemindConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;

@Entity
public class DatePlanEvent  implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public Long dayLong;//当天时间
    public Long startTime;//开始时间
    public Long endTime;//结束时间
    public String startTimeStr;//开始时间
    public String endTimeStr;//结束时间

    @Convert(columnType = String.class,converter = DatePlanConverter.class)
    public List<DatePlanBean> list;//学习计划列表

    @Convert(columnType = String.class,converter = DateRemindConverter.class)
    public List<DateRemind> remindList;//提醒事件列表
    public String repeat;//重复类型

    @Generated(hash = 1938499952)
    public DatePlanEvent(Long id, Long dayLong, Long startTime, Long endTime,
            String startTimeStr, String endTimeStr, List<DatePlanBean> list,
            List<DateRemind> remindList, String repeat) {
        this.id = id;
        this.dayLong = dayLong;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeStr = startTimeStr;
        this.endTimeStr = endTimeStr;
        this.list = list;
        this.remindList = remindList;
        this.repeat = repeat;
    }

    @Generated(hash = 1593460169)
    public DatePlanEvent() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDayLong() {
        return this.dayLong;
    }

    public void setDayLong(Long dayLong) {
        this.dayLong = dayLong;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getStartTimeStr() {
        return this.startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return this.endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public List<DatePlanBean> getList() {
        return this.list;
    }

    public void setList(List<DatePlanBean> list) {
        this.list = list;
    }

    public List<DateRemind> getRemindList() {
        return this.remindList;
    }

    public void setRemindList(List<DateRemind> remindList) {
        this.remindList = remindList;
    }

    public String getRepeat() {
        return this.repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }



}
