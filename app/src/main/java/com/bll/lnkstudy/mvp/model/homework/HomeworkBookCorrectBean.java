package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

@Entity
public class HomeworkBookCorrectBean {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= MethodManager.getAccountId();
    public int bookId;
    public int contendId;
    public String homeworkTitle;//本次作业标题
    public int page;//下标页码
    public int state=0;//状态1提交 2已完成
    public int correctMode;//批改模式
    public int scoreMode;//打分模式1打分
    public double score;//成绩
    public String answerUrl;//答案
    public String correctJson;//批改详情
    public boolean isSelfCorrect;//是否自批
    public String commitJson;//提交信息(自批时)
    public Long startTime=0L;
    @Generated(hash = 1738409563)
    public HomeworkBookCorrectBean(Long id, long userId, int bookId, int contendId,
            String homeworkTitle, int page, int state, int correctMode,
            int scoreMode, double score, String answerUrl, String correctJson,
            boolean isSelfCorrect, String commitJson, Long startTime) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.contendId = contendId;
        this.homeworkTitle = homeworkTitle;
        this.page = page;
        this.state = state;
        this.correctMode = correctMode;
        this.scoreMode = scoreMode;
        this.score = score;
        this.answerUrl = answerUrl;
        this.correctJson = correctJson;
        this.isSelfCorrect = isSelfCorrect;
        this.commitJson = commitJson;
        this.startTime = startTime;
    }
    @Generated(hash = 1773308880)
    public HomeworkBookCorrectBean() {
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
    public int getBookId() {
        return this.bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public int getContendId() {
        return this.contendId;
    }
    public void setContendId(int contendId) {
        this.contendId = contendId;
    }
    public String getHomeworkTitle() {
        return this.homeworkTitle;
    }
    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
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
    public String getAnswerUrl() {
        return this.answerUrl;
    }
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
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
    public Long getStartTime() {
        return this.startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

   

}
