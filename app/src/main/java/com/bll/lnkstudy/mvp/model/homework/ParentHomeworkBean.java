package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

public class ParentHomeworkBean {
    public int id;
    public String submitUrl;
    public String changeUrl;
    public int type;
    public int parentHomeworkId;
    @SerializedName("title")
    public String content;
    public long endTime;
    public long submitTime;
    @SerializedName("name")
    public String homeworkName;
    public int subject;
    public long time;
    @SerializedName("submitStatus")
    public int status;
    public boolean isSelector;
}
