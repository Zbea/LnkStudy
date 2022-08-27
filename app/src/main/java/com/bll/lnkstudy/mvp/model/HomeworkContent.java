package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkContent {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String bgResId;//作业背景样式id
    public int courseId;//科目id
    public int homeworkTypeId;//作业本分组id
    public long homeworkId;//作业id
    
    public long date;
    public String path;//路径
    public int page;//页码
    @Generated(hash = 1478750862)
    public HomeworkContent(Long id, long userId, String bgResId, int courseId,
            int homeworkTypeId, long homeworkId, long date, String path, int page) {
        this.id = id;
        this.userId = userId;
        this.bgResId = bgResId;
        this.courseId = courseId;
        this.homeworkTypeId = homeworkTypeId;
        this.homeworkId = homeworkId;
        this.date = date;
        this.path = path;
        this.page = page;
    }
    @Generated(hash = 547286208)
    public HomeworkContent() {
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
    public int getCourseId() {
        return this.courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public int getHomeworkTypeId() {
        return this.homeworkTypeId;
    }
    public void setHomeworkTypeId(int homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }
    public long getHomeworkId() {
        return this.homeworkId;
    }
    public void setHomeworkId(long homeworkId) {
        this.homeworkId = homeworkId;
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
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }



}
