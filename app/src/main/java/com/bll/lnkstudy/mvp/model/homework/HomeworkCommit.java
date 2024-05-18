package com.bll.lnkstudy.mvp.model.homework;

import java.util.List;

public class HomeworkCommit {

    public int index;//选中作业下标
    public int messageId;
    public String title;
    public List<Integer> contents;//选中页码的真实下标

    public int page;
    public boolean isAdd;

}
