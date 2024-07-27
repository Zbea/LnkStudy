package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

/**
 * 作业卷
 */
@Entity
public class HomeworkPaperBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    @Unique
    public int contentId;//这次作业id
    public String course;//科目
    public int typeId;//作业分组id
    public String typeName;//作业分组标题
    public int index;//（位置下标）
    public String title;//这次作业标题
    public long endTime;//老师需要学生提交时间
    public String path;//文件路径
    public int page;
    public int state;//提交状态0学生未提交1已提交未批改2已批改
    public int correctMode;//批改模式
    public int score;//成绩
    public String correctJson;//批改详情
    public boolean isSelfCorrect;
    public int scoreMode;//打分模式1打分
    public String answerUrl;
    public String commitJson;//提交信息(自批时)
    @Generated(hash = 71305048)
    public HomeworkPaperBean(Long id, long userId, int contentId, String course, int typeId,
            String typeName, int index, String title, long endTime, String path, int page, int state,
            int correctMode, int score, String correctJson, boolean isSelfCorrect, int scoreMode,
            String answerUrl, String commitJson) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.course = course;
        this.typeId = typeId;
        this.typeName = typeName;
        this.index = index;
        this.title = title;
        this.endTime = endTime;
        this.path = path;
        this.page = page;
        this.state = state;
        this.correctMode = correctMode;
        this.score = score;
        this.correctJson = correctJson;
        this.isSelfCorrect = isSelfCorrect;
        this.scoreMode = scoreMode;
        this.answerUrl = answerUrl;
        this.commitJson = commitJson;
    }
    @Generated(hash = 1573712411)
    public HomeworkPaperBean() {
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
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public String getTypeName() {
        return this.typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getEndTime() {
        return this.endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public int getCorrectMode() {
        return this.correctMode;
    }
    public void setCorrectMode(int correctMode) {
        this.correctMode = correctMode;
    }
    public int getScore() {
        return this.score;
    }
    public void setScore(int score) {
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
    public int getScoreMode() {
        return this.scoreMode;
    }
    public void setScoreMode(int scoreMode) {
        this.scoreMode = scoreMode;
    }
    public String getAnswerUrl() {
        return this.answerUrl;
    }
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }
    public String getCommitJson() {
        return this.commitJson;
    }
    public void setCommitJson(String commitJson) {
        this.commitJson = commitJson;
    }
}
