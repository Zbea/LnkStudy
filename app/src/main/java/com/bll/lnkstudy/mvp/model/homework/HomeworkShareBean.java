package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.utils.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * 分享本
 */
@Entity
public class HomeworkShareBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long studentId= MethodManager.getAccountId();
    public int typeId;
    public int type;
    public int subject;//科目
    public String name;
    public String commonName;//作业分组标题
    public String title;//这次作业标题
    public int subType;
    public long date;
    public String filePath;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> drawPaths;
    public double score;//成绩
    public String question;//批改详情
    public int questionType;//模式
    public int questionMode;//打分
    public String answerUrl;
    public int grade;
    public String examUrl;
    public String message;

    @Generated(hash = 508465887)
    public HomeworkShareBean(Long id, long studentId, int typeId, int type,
            int subject, String name, String commonName, String title, int subType,
            long date, String filePath, List<String> paths, List<String> drawPaths,
            double score, String question, int questionType, int questionMode,
            String answerUrl, int grade, String examUrl, String message) {
        this.id = id;
        this.studentId = studentId;
        this.typeId = typeId;
        this.type = type;
        this.subject = subject;
        this.name = name;
        this.commonName = commonName;
        this.title = title;
        this.subType = subType;
        this.date = date;
        this.filePath = filePath;
        this.paths = paths;
        this.drawPaths = drawPaths;
        this.score = score;
        this.question = question;
        this.questionType = questionType;
        this.questionMode = questionMode;
        this.answerUrl = answerUrl;
        this.grade = grade;
        this.examUrl = examUrl;
        this.message = message;
    }
    @Generated(hash = 874720725)
    public HomeworkShareBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getStudentId() {
        return this.studentId;
    }
    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getSubject() {
        return this.subject;
    }
    public void setSubject(int subject) {
        this.subject = subject;
    }
    public String getCommonName() {
        return this.commonName;
    }
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getSubType() {
        return this.subType;
    }
    public void setSubType(int subType) {
        this.subType = subType;
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
    public double getScore() {
        return this.score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public String getQuestion() {
        return this.question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public int getQuestionType() {
        return this.questionType;
    }
    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }
    public int getQuestionMode() {
        return this.questionMode;
    }
    public void setQuestionMode(int questionMode) {
        this.questionMode = questionMode;
    }
    public String getAnswerUrl() {
        return this.answerUrl;
    }
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public String getExamUrl() {
        return this.examUrl;
    }
    public void setExamUrl(String examUrl) {
        this.examUrl = examUrl;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
   
}
