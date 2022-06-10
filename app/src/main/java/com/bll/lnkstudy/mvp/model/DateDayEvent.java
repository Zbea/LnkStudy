package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.greendao.DateRemindConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

//重要日子事件
@Entity
public class DateDayEvent implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;
    public String title;
    public long dayLong;
    public String dayStr;
    public String explain;

    @Convert(columnType = String.class,converter = DateRemindConverter.class)
    public List<DateRemind> remindList;//提醒事件列表
    public String repeat;//重复类型

    @Generated(hash = 77519448)
    public DateDayEvent(Long id, String title, long dayLong, String dayStr,
            String explain, List<DateRemind> remindList, String repeat) {
        this.id = id;
        this.title = title;
        this.dayLong = dayLong;
        this.dayStr = dayStr;
        this.explain = explain;
        this.remindList = remindList;
        this.repeat = repeat;
    }
    @Generated(hash = 1971050405)
    public DateDayEvent() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getDayLong() {
        return this.dayLong;
    }
    public void setDayLong(long dayLong) {
        this.dayLong = dayLong;
    }
    public String getDayStr() {
        return this.dayStr;
    }
    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }
    public String getExplain() {
        return this.explain;
    }
    public void setExplain(String explain) {
        this.explain = explain;
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
