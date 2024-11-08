package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HomeworkDetailsBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String content;//内容
    public String homeworkTypeStr;//分类
    public String course;
    public long time;//提交时间
    @Generated(hash = 776758551)
    public HomeworkDetailsBean(Long id, long userId, String content, String homeworkTypeStr,
            String course, long time) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.homeworkTypeStr = homeworkTypeStr;
        this.course = course;
        this.time = time;
    }
    @Generated(hash = 663573307)
    public HomeworkDetailsBean() {
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
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getHomeworkTypeStr() {
        return this.homeworkTypeStr;
    }
    public void setHomeworkTypeStr(String homeworkTypeStr) {
        this.homeworkTypeStr = homeworkTypeStr;
    }
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }

}
