package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.DatePlanConverter;
import com.bll.lnkstudy.utils.greendao.DateRemindConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DateEvent implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public int type;//0学习计划 1日程 2重要日子

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

    @Generated(hash = 2090154417)
    public DateEvent(Long id, long userId, int type, String title, Long dayLong,
            String dayLongStr, String explain, Long startTime, Long endTime,
            String startTimeStr, String endTimeStr, List<DatePlanBean> list,
            List<DateRemind> remindList, String repeat) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.dayLong = dayLong;
        this.dayLongStr = dayLongStr;
        this.explain = explain;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeStr = startTimeStr;
        this.endTimeStr = endTimeStr;
        this.list = list;
        this.remindList = remindList;
        this.repeat = repeat;
    }

    @Generated(hash = 1511002217)
    public DateEvent() {
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getDayLong() {
        return this.dayLong;
    }
    public void setDayLong(Long dayLong) {
        this.dayLong = dayLong;
    }
    public String getDayLongStr() {
        return this.dayLongStr;
    }
    public void setDayLongStr(String dayLongStr) {
        this.dayLongStr = dayLongStr;
    }
    public String getExplain() {
        return this.explain;
    }
    public void setExplain(String explain) {
        this.explain = explain;
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
