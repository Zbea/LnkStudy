package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import java.util.Objects;

/**
 * 作业卷
 */
@Entity
public class HomeworkPaperBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    @Unique
    public int contentId;//这次作业id
    public String course;//科目
    public int homeworkTypeId;
    public int typeId;//作业分组id
    public String typeName;//作业分组标题
    public String title;//这次作业标题
    public Long endTime=0L;//老师需要学生提交时间
    public Long startDate=0L;
    public long date;
    public String filePath;//文件路径
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> drawPaths;
    public int state;//提交状态0学生未提交1已提交未批改2已批改
    public boolean isHomework;
    public int correctMode;//批改模式
    public double score;//成绩
    public String correctJson;//批改详情
    public boolean isSelfCorrect;
    public int scoreMode;//打分模式1打分
    public String answerUrl;
    public String commitJson;//提交信息(自批时)
    public String message;

    @Generated(hash = 1722412922)
    public HomeworkPaperBean(Long id, long userId, int contentId, String course,
            int homeworkTypeId, int typeId, String typeName, String title,
            Long endTime, Long startDate, long date, String filePath,
            List<String> paths, List<String> drawPaths, int state,
            boolean isHomework, int correctMode, double score, String correctJson,
            boolean isSelfCorrect, int scoreMode, String answerUrl,
            String commitJson, String message) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.course = course;
        this.homeworkTypeId = homeworkTypeId;
        this.typeId = typeId;
        this.typeName = typeName;
        this.title = title;
        this.endTime = endTime;
        this.startDate = startDate;
        this.date = date;
        this.filePath = filePath;
        this.paths = paths;
        this.drawPaths = drawPaths;
        this.state = state;
        this.isHomework = isHomework;
        this.correctMode = correctMode;
        this.score = score;
        this.correctJson = correctJson;
        this.isSelfCorrect = isSelfCorrect;
        this.scoreMode = scoreMode;
        this.answerUrl = answerUrl;
        this.commitJson = commitJson;
        this.message = message;
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
    public int getHomeworkTypeId() {
        return this.homeworkTypeId;
    }
    public void setHomeworkTypeId(int homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getEndTime() {
        return this.endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    public Long getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public List<String> getPaths() {
        return this.paths;
    }
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
    public List<String> getDrawPaths() {
        return this.drawPaths;
    }
    public void setDrawPaths(List<String> drawPaths) {
        this.drawPaths = drawPaths;
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
    public int getCorrectMode() {
        return this.correctMode;
    }
    public void setCorrectMode(int correctMode) {
        this.correctMode = correctMode;
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
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
