package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkType implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public String name;
    public int type;//
    public Long date; //创建时间
    public int resId; //作业本内容背景id
    public int bgResId;//当前作业本背景样式id
    public int courseId;//科目id
    public boolean isPg;//是否收到批改
    public boolean isListenToRead;//是否是听读
    @Generated(hash = 579060016)
    public HomeworkType(Long id, String name, int type, Long date, int resId,
            int bgResId, int courseId, boolean isPg, boolean isListenToRead) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.resId = resId;
        this.bgResId = bgResId;
        this.courseId = courseId;
        this.isPg = isPg;
        this.isListenToRead = isListenToRead;
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
    public Long getDate() {
        return this.date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public int getResId() {
        return this.resId;
    }
    public void setResId(int resId) {
        this.resId = resId;
    }
    public int getBgResId() {
        return this.bgResId;
    }
    public void setBgResId(int bgResId) {
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
