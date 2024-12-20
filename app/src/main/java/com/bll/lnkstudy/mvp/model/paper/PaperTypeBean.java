package com.bll.lnkstudy.mvp.model.paper;

import androidx.annotation.Nullable;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaperTypeBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long studentId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public long teacherId;//老师id
    public String teacher;
    public String name;//考卷分类
    @Unique
    public int typeId;
    public String course;
    public long date;//创建时间
    public int grade;
    public boolean isCloud;
    public Integer createStatus;//1当前考试本
    @SerializedName("addType")
    public Integer autoState;//1生成作业本 0创建作业本
    @Transient
    public boolean isPg;//是否批改
    @Transient
    public double score;
    @Transient
    public String paperTitle;
    @Transient
    public int cloudId;
    @Transient
    public String downloadUrl;
    @Transient
    public String contentJson;
    @Transient
    public String contentSubtypeJson;

    @Generated(hash = 104583001)
    public PaperTypeBean(Long id, long studentId, long teacherId, String teacher, String name, int typeId, String course, long date, int grade,
            boolean isCloud, Integer createStatus, Integer autoState) {
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.teacher = teacher;
        this.name = name;
        this.typeId = typeId;
        this.course = course;
        this.date = date;
        this.grade = grade;
        this.isCloud = isCloud;
        this.createStatus = createStatus;
        this.autoState = autoState;
    }





    @Generated(hash = 26624406)
    public PaperTypeBean() {
    }





    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof PaperTypeBean))
            return false;
        if (this==obj)
            return true;
        PaperTypeBean item=(PaperTypeBean) obj;
        return Objects.equals(this.id, item.id)&&this.studentId==item.studentId && Objects.equals(this.name, item.name) &&this.teacherId==item.teacherId
                &&this.grade==item.grade&&this.typeId==item.typeId;
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





    public long getTeacherId() {
        return this.teacherId;
    }





    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }





    public String getTeacher() {
        return this.teacher;
    }





    public void setTeacher(String teacher) {
        this.teacher = teacher;
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





    public long getDate() {
        return this.date;
    }





    public void setDate(long date) {
        this.date = date;
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





    public Integer getCreateStatus() {
        return this.createStatus;
    }





    public void setCreateStatus(Integer createStatus) {
        this.createStatus = createStatus;
    }





    public Integer getAutoState() {
        return this.autoState;
    }





    public void setAutoState(Integer autoState) {
        this.autoState = autoState;
    }
    
}
