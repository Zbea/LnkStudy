package com.bll.lnkstudy.mvp.model.paper;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class PaperTypeBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long studentId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public long userId;//老师id
    public String name;//考卷分类
    @Unique
    public int typeId;
    public String course;
    public long date;//创建时间
    public int grade;
    @Transient
    public boolean isPg;//是否批改
    @Transient
    public int score;
    public boolean isCloud;
    public int cloudId;

    @Generated(hash = 1307660412)
    public PaperTypeBean(Long id, long studentId, long userId, String name, int typeId, String course, long date,
            int grade, boolean isCloud, int cloudId) {
        this.id = id;
        this.studentId = studentId;
        this.userId = userId;
        this.name = name;
        this.typeId = typeId;
        this.course = course;
        this.date = date;
        this.grade = grade;
        this.isCloud = isCloud;
        this.cloudId = cloudId;
    }
    @Generated(hash = 26624406)
    public PaperTypeBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getStudentId() {
        return this.studentId;
    }
    public void setStudentId(long studentId) {
        this.studentId = studentId;
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
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public boolean getIsCloud() {
        return this.isCloud;
    }
    public void setIsCloud(boolean isCloud) {
        this.isCloud = isCloud;
    }
    public int getCloudId() {
        return this.cloudId;
    }
    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    
}
