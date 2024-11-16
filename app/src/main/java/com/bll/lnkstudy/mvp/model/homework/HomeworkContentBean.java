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
    public int state=0;//0未提交1已提交2已批改
    public Long date=0L;
    public Long commitDate=0L;
    public Long startDate=0L;
    public String path;//文件路径
    public int page;//页码
    public int correctMode;//批改模式
    public int scoreMode;//打分模式1打分
    public double score;//成绩
    public String correctJson;//批改详情
    public boolean isSelfCorrect;//是否自批
    public String commitJson;//提交信息(自批时)
    public String answerUrl;
    @Generated(hash = 1053978476)
    public HomeworkContentBean(Long id, long userId, String bgResId, String course, int homeworkTypeId,
            String typeStr, int contentId, String title, int state, Long date, Long commitDate,
            Long startDate, String path, int page, int correctMode, int scoreMode, double score,
            String correctJson, boolean isSelfCorrect, String commitJson, String answerUrl) {
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
        this.startDate = startDate;
        this.path = path;
        this.page = page;
        this.correctMode = correctMode;
        this.scoreMode = scoreMode;
        this.score = score;
        this.correctJson = correctJson;
        this.isSelfCorrect = isSelfCorrect;
        this.commitJson = commitJson;
        this.answerUrl = answerUrl;
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
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
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
    public Long getDate() {
        return this.date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public Long getCommitDate() {
        return this.commitDate;
    }
    public void setCommitDate(Long commitDate) {
        this.commitDate = commitDate;
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
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
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
    public boolean getIsSelfCorrect() {
        return this.isSelfCorrect;
    }
    public void setIsSelfCorrect(boolean isSelfCorrect) {
        this.isSelfCorrect = isSelfCorrect;
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

}
