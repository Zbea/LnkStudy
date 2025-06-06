package com.bll.lnkstudy.mvp.model.homework;

import androidx.annotation.Nullable;

import com.bll.lnkstudy.MethodManager;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkTypeBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long studentId = MethodManager.getAccountId();
    @SerializedName("userId")
    public Long teacherId;
    public String teacher;
    public String name;
    public int grade;//年级
    public int typeId;//作业本分类id
    @SerializedName("subType")
    public int state;//1作业卷 2普通作业本 3听读本4题卷本 5错题本 6练字本 7手写本 8阅读本 9分享本
    public long date; //创建时间
    public String contentResId; //作业本内容背景id
    public String bgResId;//当前作业本背景样式id
    public String course;
    public int bookId;
    public int createStatus=0;//自建作业本0家长创建1老师创建2自动创建错题本3自动创建分享本4
    public int fromStatus=0;//家长创建1老师创建2
    public int messageTotal=0;//作业消息数目
    public boolean isCloud;
    @SerializedName("addType")
    public int autoState=0;//1生成作业本 0创建作业本
    @Transient
    public int cloudId;
    @Transient
    public String downloadUrl;
    @Transient
    public String zipUrl;
    @Transient
    public String contentJson;
    @Transient
    public String contentSubtypeJson;
    @Transient
    public boolean isCorrect;//是否收到批改
    @Transient
    public boolean isMessage;//收到通知
    @Transient
    public boolean isShare;//收到分享
    @Transient
    public List<?> messages;

    @Generated(hash = 1072702211)
    public HomeworkTypeBean(Long id, long studentId, Long teacherId, String teacher, String name, int grade, int typeId, int state, long date, String contentResId,
            String bgResId, String course, int bookId, int createStatus, int fromStatus, int messageTotal, boolean isCloud, int autoState) {
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.teacher = teacher;
        this.name = name;
        this.grade = grade;
        this.typeId = typeId;
        this.state = state;
        this.date = date;
        this.contentResId = contentResId;
        this.bgResId = bgResId;
        this.course = course;
        this.bookId = bookId;
        this.createStatus = createStatus;
        this.fromStatus = fromStatus;
        this.messageTotal = messageTotal;
        this.isCloud = isCloud;
        this.autoState = autoState;
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
    public long getStudentId() {
        return this.studentId;
    }
    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
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

    public String getCourse() {
        return this.course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getBookId() {
        return this.bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCreateStatus() {
        return this.createStatus;
    }

    public void setCreateStatus(int createStatus) {
        this.createStatus = createStatus;
    }

    public int getMessageTotal() {
        return this.messageTotal;
    }
    public void setMessageTotal(int messageTotal) {
        this.messageTotal = messageTotal;
    }
    public boolean getIsCloud() {
        return this.isCloud;
    }
    public void setIsCloud(boolean isCloud) {
        this.isCloud = isCloud;
    }
    public String getTeacher() {
        return this.teacher;
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    public Long getTeacherId() {
        return this.teacherId;
    }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof HomeworkTypeBean))
            return false;
        if (this==obj)
            return true;
        HomeworkTypeBean item=(HomeworkTypeBean) obj;
        return Objects.equals(this.id, item.id)&&this.studentId==item.studentId && Objects.equals(this.name, item.name)&& Objects.equals(this.teacherId, item.teacherId)
                &&this.grade==item.grade&&this.typeId==item.typeId&&this.state==item.state;
    }


    public Integer getAutoState() {
        return this.autoState;
    }


    public void setAutoState(Integer autoState) {
        this.autoState = autoState;
    }


    public void setAutoState(int autoState) {
        this.autoState = autoState;
    }


    public int getFromStatus() {
        return this.fromStatus;
    }


    public void setFromStatus(int fromStatus) {
        this.fromStatus = fromStatus;
    }

}
