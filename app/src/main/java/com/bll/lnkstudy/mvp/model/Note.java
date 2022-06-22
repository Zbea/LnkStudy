package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Note implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    public long date;//创建时间
    public long nowDate;//最近查看时间
    public String title;
    public int type;

    @Generated(hash = 1531424357)
    public Note(Long id, long date, long nowDate, String title, int type) {
        this.id = id;
        this.date = date;
        this.nowDate = nowDate;
        this.title = title;
        this.type = type;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
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
    public long getNowDate() {
        return this.nowDate;
    }
    public void setNowDate(long nowDate) {
        this.nowDate = nowDate;
    }

}
