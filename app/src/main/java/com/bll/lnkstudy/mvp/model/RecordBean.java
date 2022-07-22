package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecordBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public String title;
    public long date;
    public String path;
    public int courseId;
    @Transient
    public int state=0;//播放状态

    @Generated(hash = 255914155)
    public RecordBean(Long id, String title, long date, String path, int courseId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.path = path;
        this.courseId = courseId;
    }
    @Generated(hash = 96196931)
    public RecordBean() {
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
    public int getCourseId() {
        return this.courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

}
