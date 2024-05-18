package com.bll.lnkstudy.mvp.model.paper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamScoreItem implements Serializable {
    public String score;
    public int sort;
    public List<ExamScoreItem> childScores=new ArrayList<>();
}
