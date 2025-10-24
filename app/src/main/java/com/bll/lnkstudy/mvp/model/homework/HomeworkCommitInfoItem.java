package com.bll.lnkstudy.mvp.model.homework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业提交信息保存
 */
public class HomeworkCommitInfoItem implements Serializable {

    public int homeworkTypeId;
    public int typeId;//作业本分类id
    public int state;//1作业卷 2普通作业本 3听读本4题卷本 5错题本
    public String course;
    public int bookId;
    public int messageId;//作业消息id
    public String title;
    public int submitState;//0提交1不提交
    public int standardTime;//标准时长
    public long takeTime;//时长
    public List<Integer> contents;//选中页码的真实下标
    public boolean isSelfCorrect;
    public int correctMode;//批改模式
    public int scoreMode;//打分模式1打分
    public double score;//成绩
    public String correctJson;//批改详情
    public String answerUrl;
    public String commitUrl;//提交图片地址
    public String commitDrawUrl;
    public List<String> paths=new ArrayList<>();//合图
    public List<String> drawPaths=new ArrayList<>();//作业卷手写图
}
