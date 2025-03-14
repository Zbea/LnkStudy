package com.bll.lnkstudy.mvp.model.paper;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.DateWeekConverter;
import com.bll.lnkstudy.utils.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;
import java.util.Objects;

/**
 * 本次考试
 */
@Entity
public class PaperBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public int contentId;//这次考卷id
    public String course;//科目
    public int paperTypeId;//本地测卷分类id
    public int typeId;//考卷分组id
    public int grade;
    public String typeName;//考卷分组标题
    public String title;
    public String filePath;//文件路径
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> drawPaths;
    public long date;
    public int correctMode;//批改模式
    public String score;//成绩
    public String correctJson;//批改详情
    public String answerUrl;
    public int scoreMode;
    @Generated(hash = 117013680)
    public PaperBean(Long id, long userId, int contentId, String course,
            int paperTypeId, int typeId, int grade, String typeName, String title,
            String filePath, List<String> paths, List<String> drawPaths, long date,
            int correctMode, String score, String correctJson, String answerUrl,
            int scoreMode) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.course = course;
        this.paperTypeId = paperTypeId;
        this.typeId = typeId;
        this.grade = grade;
        this.typeName = typeName;
        this.title = title;
        this.filePath = filePath;
        this.paths = paths;
        this.drawPaths = drawPaths;
        this.date = date;
        this.correctMode = correctMode;
        this.score = score;
        this.correctJson = correctJson;
        this.answerUrl = answerUrl;
        this.scoreMode = scoreMode;
    }
    @Generated(hash = 1608836968)
    public PaperBean() {
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
    public int getPaperTypeId() {
        return this.paperTypeId;
    }
    public void setPaperTypeId(int paperTypeId) {
        this.paperTypeId = paperTypeId;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
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
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
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
    public String getAnswerUrl() {
        return this.answerUrl;
    }
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }
    public int getScoreMode() {
        return this.scoreMode;
    }
    public void setScoreMode(int scoreMode) {
        this.scoreMode = scoreMode;
    }

}
