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
public class RecordBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int homeworkTypeId;
    public int typeId;//作业分组id
    public String typeName;//作业分组标题
    public String title;
    public long date;
    public String path;
    public String course;
    public boolean isCommit;
    @Transient
    public int state=0;//播放状态

    @Generated(hash = 1221133260)
    public RecordBean(Long id, long userId, int homeworkTypeId, int typeId, String typeName,
            String title, long date, String path, String course, boolean isCommit) {
        this.id = id;
        this.userId = userId;
        this.homeworkTypeId = homeworkTypeId;
        this.typeId = typeId;
        this.typeName = typeName;
        this.title = title;
        this.date = date;
        this.path = path;
        this.course = course;
        this.isCommit = isCommit;
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
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public boolean getIsCommit() {
        return this.isCommit;
    }
    public void setIsCommit(boolean isCommit) {
        this.isCommit = isCommit;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getHomeworkTypeId() {
        return this.homeworkTypeId;
    }
    public void setHomeworkTypeId(int homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }
    public String getTypeName() {
        return this.typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
}
