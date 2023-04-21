package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaintingTypeBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int type;//类型
    public int grade;
    public long date;
    @Generated(hash = 875670369)
    public PaintingTypeBean(Long id, long userId, int type, int grade, long date) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.grade = grade;
        this.date = date;
    }
    @Generated(hash = 1478960882)
    public PaintingTypeBean() {
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
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
   
}
