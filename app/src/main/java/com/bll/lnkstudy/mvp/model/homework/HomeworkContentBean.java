package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class HomeworkContentBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String bgResId;//作业背景样式id
    public String course;//科目
    public int homeworkTypeId;//作业本分组id
    public String typeStr;//作业本分类
    public int contentId;//老师下发作业id
    public String title;
    public int state;//0未提交1已提交2已批改
    public long date;
    public long commitDate;
    public String path;//文件路径
    public int page;//页码

    @Generated(hash = 4859767)
    public HomeworkContentBean(Long id, long userId, String bgResId, String course, int homeworkTypeId,
            String typeStr, int contentId, String title, int state, long date, long commitDate,
            String path, int page) {
        this.id = id;
        this.userId = userId;
        this.bgResId = bgResId;
        this.course = course;
        this.homeworkTypeId = homeworkTypeId;
        this.typeStr = typeStr;
        this.contentId = contentId;
        this.title = title;
        this.state = state;
        this.date = date;
        this.commitDate = commitDate;
        this.path = path;
        this.page = page;
    }
    @Generated(hash = 1693358578)
    public HomeworkContentBean() {
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
    public int getHomeworkTypeId() {
        return this.homeworkTypeId;
    }
    public void setHomeworkTypeId(int homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public long getCommitDate() {
        return this.commitDate;
    }
    public void setCommitDate(long commitDate) {
        this.commitDate = commitDate;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }


}
