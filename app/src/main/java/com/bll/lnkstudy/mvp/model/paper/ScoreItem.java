package com.bll.lnkstudy.mvp.model.paper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScoreItem implements Serializable {
    public double score=0;
    public int sort;
    public String sortStr;
    public int rootSort;
    public int level;
    public int result;//0错1对
    public boolean isResultShow=true;
    public double label;//题目标准分数
    public List<ScoreItem> childScores=new ArrayList<>();
}
