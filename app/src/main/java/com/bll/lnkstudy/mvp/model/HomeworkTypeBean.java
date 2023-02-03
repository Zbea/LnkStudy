package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkTypeBean implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String name;
    public int typeId;//作业本分类id
    public int state=0;//0普通作业本 1听读本 2题卷本 3课辅习题本
    public long date; //创建时间
    public String contentResId; //作业本内容背景id
    public String bgResId;//当前作业本背景样式id
    public int courseId;//科目id
    public String course;
    public boolean isCreate;//自建作业本
    @Transient
    public boolean isPg;//是否收到批改
    @Transient
    public boolean isMessage;//收到通知
    @Transient
    public HomeworkMessage message;
    @Generated(hash = 743607381)
    public HomeworkTypeBean(Long id, long userId, String name, int typeId,
            int state, long date, String contentResId, String bgResId, int courseId,
            String course, boolean isCreate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.typeId = typeId;
        this.state = state;
        this.date = date;
        this.contentResId = contentResId;
        this.bgResId = bgResId;
        this.courseId = courseId;
        this.course = course;
        this.isCreate = isCreate;
    }
    @Generated(hash = 1652492346)
    public HomeworkTypeBean() {
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
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getContentResId() {
        return this.contentResId;
    }
    public void setContentResId(String contentResId) {
        this.contentResId = contentResId;
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
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public boolean getIsCreate() {
        return this.isCreate;
    }
    public void setIsCreate(boolean isCreate) {
        this.isCreate = isCreate;
    }

}
