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

    public String title;
    public int state;//0未提交1已提交2已批改
    public long date;
    public long commitDate;
    public String folderPath;//文件夹路径
    public String filePath;//文件路径
    public String pathName;//文件名
    public int page;//页码

    @Generated(hash = 2006166801)
    public HomeworkContent(Long id, long userId, String bgResId, int courseId,
            int homeworkTypeId, String title, int state, long date, long commitDate,
            String folderPath, String filePath, String pathName, int page) {
        this.id = id;
        this.userId = userId;
        this.bgResId = bgResId;
        this.courseId = courseId;
        this.homeworkTypeId = homeworkTypeId;
        this.title = title;
        this.state = state;
        this.date = date;
        this.commitDate = commitDate;
        this.folderPath = folderPath;
        this.filePath = filePath;
        this.pathName = pathName;
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
    public String getFolderPath() {
        return this.folderPath;
    }
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getPathName() {
        return this.pathName;
    }
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }



}
