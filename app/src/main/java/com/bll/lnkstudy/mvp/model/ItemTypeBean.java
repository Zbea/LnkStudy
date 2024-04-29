package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ItemTypeBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String title;
    public int type;//1截图2笔记3画本4书法
    public long date;
    public String path;
    public int grade;
    @Transient
    public boolean isCheck;

    @Generated(hash = 516443006)
    public ItemTypeBean(Long id, long userId, String title, int type, long date, String path,
            int grade) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.path = path;
        this.grade = grade;
    }
    @Generated(hash = 2077540725)
    public ItemTypeBean() {
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }


}
