package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.greendao.DateRemindConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * 日历事件日程
 */
@Entity
public class DateScheduleEvent implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;
    public int type;//类型
    public String scheduleTitle;
    public long scheduleDay;//当天时间
    public long scheduleStartTime;//开始时间
    public long scheduleEndTime;//结束时间
    public String scheduleStartTimeStr;//开始时间
    public String scheduleEndTimeStr;//结束时间

    @Convert(columnType = String.class,converter = DateRemindConverter.class)
    public List<DateRemind> remindList;//提醒事件列表
    public String repeat;//重复类型

    @Generated(hash = 1897524648)
    public DateScheduleEvent(Long id, int type, String scheduleTitle,
                             long scheduleDay, long scheduleStartTime, long scheduleEndTime,
                             String scheduleStartTimeStr, String scheduleEndTimeStr,
                             List<DateRemind> remindList, String repeat) {
        this.id = id;
        this.type = type;
        this.scheduleTitle = scheduleTitle;
        this.scheduleDay = scheduleDay;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.scheduleStartTimeStr = scheduleStartTimeStr;
        this.scheduleEndTimeStr = scheduleEndTimeStr;
        this.remindList = remindList;
        this.repeat = repeat;
    }
    @Generated(hash = 223663430)
    public DateScheduleEvent() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getScheduleTitle() {
        return this.scheduleTitle;
    }
    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }
    public long getScheduleDay() {
        return this.scheduleDay;
    }
    public void setScheduleDay(long scheduleDay) {
        this.scheduleDay = scheduleDay;
    }
    public long getScheduleStartTime() {
        return this.scheduleStartTime;
    }
    public void setScheduleStartTime(long scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }
    public long getScheduleEndTime() {
        return this.scheduleEndTime;
    }
    public void setScheduleEndTime(long scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }
    public String getScheduleStartTimeStr() {
        return this.scheduleStartTimeStr;
    }
    public void setScheduleStartTimeStr(String scheduleStartTimeStr) {
        this.scheduleStartTimeStr = scheduleStartTimeStr;
    }
    public String getScheduleEndTimeStr() {
        return this.scheduleEndTimeStr;
    }
    public void setScheduleEndTimeStr(String scheduleEndTimeStr) {
        this.scheduleEndTimeStr = scheduleEndTimeStr;
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
