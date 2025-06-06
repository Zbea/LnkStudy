package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecordBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public int homeworkTypeId;
    public String typeName;//作业分组标题
    public String course;
    public String title;
    public int contendId;
    public long date;
    public String path;
    public int second;
    public boolean isHomework=false;
    public String question;
    public int correctModule;
    public double score;
    public boolean isCorrect;
    @Transient
    public int state=0;//播放状态

    @Generated(hash = 1447355199)
    public RecordBean(Long id, long userId, int homeworkTypeId, String typeName,
            String course, String title, int contendId, long date, String path,
            int second, boolean isHomework, String question, int correctModule,
            double score, boolean isCorrect) {
        this.id = id;
        this.userId = userId;
        this.homeworkTypeId = homeworkTypeId;
        this.typeName = typeName;
        this.course = course;
        this.title = title;
        this.contendId = contendId;
        this.date = date;
        this.path = path;
        this.second = second;
        this.isHomework = isHomework;
        this.question = question;
        this.correctModule = correctModule;
        this.score = score;
        this.isCorrect = isCorrect;
    }
    @Generated(hash = 96196931)
    public RecordBean() {
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
    public int getHomeworkTypeId() {
        return this.homeworkTypeId;
    }
    public void setHomeworkTypeId(int homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }
    public String getTypeName() {
        return this.typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getContendId() {
        return this.contendId;
    }
    public void setContendId(int contendId) {
        this.contendId = contendId;
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
    public int getSecond() {
        return this.second;
    }
    public void setSecond(int second) {
        this.second = second;
    }
    public boolean getIsHomework() {
        return this.isHomework;
    }
    public void setIsHomework(boolean isHomework) {
        this.isHomework = isHomework;
    }
    public String getQuestion() {
        return this.question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public double getScore() {
        return this.score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public boolean getIsCorrect() {
        return this.isCorrect;
    }
    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    public int getCorrectModule() {
        return this.correctModule;
    }
    public void setCorrectModule(int correctModule) {
        this.correctModule = correctModule;
    }
    
}
