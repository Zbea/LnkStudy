package com.bll.lnkstudy.mvp.model.paper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamScoreItem implements Serializable {
    public String score="0";
    public int sort;
    public int result;//0错1对
    public int label;//题目标准分数
    public List<ExamScoreItem> childScores=new ArrayList<>();
}
