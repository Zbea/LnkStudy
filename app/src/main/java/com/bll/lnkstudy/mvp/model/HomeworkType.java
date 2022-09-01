package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkType implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String name;
    public int type;//作业本分类id
    public long date; //创建时间
    public String resId; //作业本内容背景id
    public String bgResId;//当前作业本背景样式id
    public int courseId;//科目id
    @Transient
    public boolean isPg;//是否收到批改
    @Transient
    public boolean isListenToRead;//是否是听读
    @Transient
    public boolean isMessage;//收到通知
    @Transient
    public HomeworkMessage message;

    @Generated(hash = 1161804354)
    public HomeworkType(Long id, long userId, String name, int type, long date,
            String resId, String bgResId, int courseId) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.date = date;
        this.resId = resId;
        this.bgResId = bgResId;
        this.courseId = courseId;
    }
    @Generated(hash = 302760485)
    public HomeworkType() {
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
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getResId() {
        return this.resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public String getBgResId() {
        return this.bgResId;
    }
    public void setBgResId(String bgResId) {
        this.bgResId = bgResId;
    }
    public int getCourseId() {
        return this.courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public boolean getIsPg() {
        return this.isPg;
    }
    public void setIsPg(boolean isPg) {
        this.isPg = isPg;
    }
    public boolean getIsListenToRead() {
        return this.isListenToRead;
    }
    public void setIsListenToRead(boolean isListenToRead) {
        this.isListenToRead = isListenToRead;
    }


}
