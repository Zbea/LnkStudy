package com.bll.lnkstudy.mvp.model.homework;

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
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int bookId;
    public String homeworkTitle;//本次作业标题
    public String pages;//下标页码
    public int correctMode;//批改模式
    public String score;//成绩
    public String correctJson;//批改详情
    @Generated(hash = 65183209)
    public HomeworkBookCorrectBean(Long id, long userId, int bookId, String homeworkTitle, String pages,
            int correctMode, String score, String correctJson) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.homeworkTitle = homeworkTitle;
        this.pages = pages;
        this.correctMode = correctMode;
        this.score = score;
        this.correctJson = correctJson;
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
    public String getHomeworkTitle() {
        return this.homeworkTitle;
    }
    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }
    public String getPages() {
        return this.pages;
    }
    public void setPages(String pages) {
        this.pages = pages;
    }
    public int getCorrectMode() {
        return this.correctMode;
    }
    public void setCorrectMode(int correctMode) {
        this.correctMode = correctMode;
    }
    public String getScore() {
        return this.score;
    }
    public void setScore(String score) {
        this.score = score;
    }
    public String getCorrectJson() {
        return this.correctJson;
    }
    public void setCorrectJson(String correctJson) {
        this.correctJson = correctJson;
    }

}
