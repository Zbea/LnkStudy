package com.bll.lnkstudy.mvp.model.date;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.DateLongConverter;
import com.bll.lnkstudy.utils.greendao.DatePlanConverter;
import com.bll.lnkstudy.utils.greendao.DateWeekConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DateEventBean implements Serializable ,Cloneable{

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int type;//0学习计划 1重要日子
    public int date;//0星期1日期

    public String title;//标题
    public long dayLong;//当天时间
    public long maxLong;//最迟日期
    public String dayLongStr;
    public String explain;//说明

    public boolean isCountdown;//是否开启倒计时
    public boolean isRemind;//是否提醒
    public int remindDay=1;//天数 提前多少天提醒

    @Convert(columnType = String.class,converter = DateWeekConverter.class)
    public List<DateWeek> weeks;

    @Convert(columnType = String.class,converter = DatePlanConverter.class)
    public List<DatePlan> plans;//学习计划列表

    @Convert(columnType = String.class,converter = DateLongConverter.class)
    public List<Long> dates;//日期

    @Transient
    public boolean isCheck;


    @Generated(hash = 538798624)
    public DateEventBean(Long id, long userId, int type, int date, String title, long dayLong,
            long maxLong, String dayLongStr, String explain, boolean isCountdown, boolean isRemind,
            int remindDay, List<DateWeek> weeks, List<DatePlan> plans, List<Long> dates) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.date = date;
        this.title = title;
        this.dayLong = dayLong;
        this.maxLong = maxLong;
        this.dayLongStr = dayLongStr;
        this.explain = explain;
        this.isCountdown = isCountdown;
        this.isRemind = isRemind;
        this.remindDay = remindDay;
        this.weeks = weeks;
        this.plans = plans;
        this.dates = dates;
    }

    @Generated(hash = 1490863088)
    public DateEventBean() {
    }

    
    @Override
    public Object clone()  {
        DateEventBean a = null;
        try {
            a = (DateEventBean) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
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

    public boolean getIsCountdown() {
        return this.isCountdown;
    }

    public void setIsCountdown(boolean isCountdown) {
        this.isCountdown = isCountdown;
    }

    public boolean getIsRemind() {
        return this.isRemind;
    }

    public void setIsRemind(boolean isRemind) {
        this.isRemind = isRemind;
    }

    public int getRemindDay() {
        return this.remindDay;
    }

    public void setRemindDay(int remindDay) {
        this.remindDay = remindDay;
    }

    public List<DateWeek> getWeeks() {
        return this.weeks;
    }

    public void setWeeks(List<DateWeek> weeks) {
        this.weeks = weeks;
    }

    public List<DatePlan> getPlans() {
        return this.plans;
    }

    public void setPlans(List<DatePlan> plans) {
        this.plans = plans;
    }

    public int getDate() {
        return this.date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setDayLong(long dayLong) {
        this.dayLong = dayLong;
    }

    public List<Long> getDates() {
        return this.dates;
    }

    public void setDates(List<Long> dates) {
        this.dates = dates;
    }

    public long getMaxLong() {
        return this.maxLong;
    }

    public void setMaxLong(long maxLong) {
        this.maxLong = maxLong;
    }



}
