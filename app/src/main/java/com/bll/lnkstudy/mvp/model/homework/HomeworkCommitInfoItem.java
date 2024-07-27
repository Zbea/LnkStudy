package com.bll.lnkstudy.mvp.model.homework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业提交信息保存
 */
public class HomeworkCommitInfoItem implements Serializable {

    public int index;//选中作业下标
    public int typeId;//作业本分类id
    public String typeName;//作业本名称
    public String course;
    public int state;//1作业卷 2普通作业本 3听读本4题卷本 5错题本
    public int bookId;
    public int messageId;//作业消息id
    public String title;
    public List<Integer> contents;//选中页码的真实下标
    public boolean isSelfCorrect;
    public int correctMode;//批改模式
    public int scoreMode;//打分模式1打分
    public int score;//成绩
    public String correctJson;//批改详情
    public String answerUrl;
    public List<String> paths=new ArrayList<>();

}
