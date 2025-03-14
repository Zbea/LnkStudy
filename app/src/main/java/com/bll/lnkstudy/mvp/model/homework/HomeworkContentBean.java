package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.MethodManager;
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
    public long userId= MethodManager.getAccountId();
    public String course;//科目
    public int homeworkTypeId;//作业本分组id
    public int fromStatus;//1家长2老师
    public String typeName;//作业本分类
    public int contentId;//老师下发作业id
    public String title;
    public int state=0;//0未提交1已提交2已批改
    public boolean isHomework=false;//true为这个布置作业
    public Long date=0L;
    public Long startDate=0L;
    public String path;//文件路径
    public double score;//成绩
    public String correctJson;//批改详情
    public String commitJson;//提交信息(自批时)
    public String answerUrl;
    public int correctMode;//批改模式
    public int scoreMode;//打分模式1打分
    @Generated(hash = 1645804377)
    public HomeworkContentBean(Long id, long userId, String course,
            int homeworkTypeId, int fromStatus, String typeName, int contentId,
            String title, int state, boolean isHomework, Long date, Long startDate,
            String path, double score, String correctJson, String commitJson,
            String answerUrl, int correctMode, int scoreMode) {
        this.id = id;
        this.userId = userId;
        this.course = course;
        this.homeworkTypeId = homeworkTypeId;
        this.fromStatus = fromStatus;
        this.typeName = typeName;
        this.contentId = contentId;
        this.title = title;
        this.state = state;
        this.isHomework = isHomework;
        this.date = date;
        this.startDate = startDate;
        this.path = path;
        this.score = score;
        this.correctJson = correctJson;
        this.commitJson = commitJson;
        this.answerUrl = answerUrl;
        this.correctMode = correctMode;
        this.scoreMode = scoreMode;
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
    public int getFromStatus() {
        return this.fromStatus;
    }
    public void setFromStatus(int fromStatus) {
        this.fromStatus = fromStatus;
    }
    public String getTypeName() {
        return this.typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
    public boolean getIsHomework() {
        return this.isHomework;
    }
    public void setIsHomework(boolean isHomework) {
        this.isHomework = isHomework;
    }
    public Long getDate() {
        return this.date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public Long getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public double getScore() {
        return this.score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public String getCorrectJson() {
        return this.correctJson;
    }
    public void setCorrectJson(String correctJson) {
        this.correctJson = correctJson;
    }
    public String getCommitJson() {
        return this.commitJson;
    }
    public void setCommitJson(String commitJson) {
        this.commitJson = commitJson;
    }
    public String getAnswerUrl() {
        return this.answerUrl;
    }
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }
    public int getCorrectMode() {
        return this.correctMode;
    }
    public void setCorrectMode(int correctMode) {
        this.correctMode = correctMode;
    }
    public int getScoreMode() {
        return this.scoreMode;
    }
    public void setScoreMode(int scoreMode) {
        this.scoreMode = scoreMode;
    }

}
