package com.bll.lnkstudy.mvp.model.paper;

import com.google.gson.annotations.SerializedName;

/**
 * 考试下载
 */
public class ExamCorrectBean {
    public int id;
    public int schoolExamJobId;
    public String examUrl;
    public String studentUrl;
    public String teacherUrl;
    public double score;
    public String question;
    public int questionType;
    public int questionMode;
    public String answerUrl;
    @SerializedName("examName")
    public String typeName;//收到考卷 分类名称
    public int typeId;
}
