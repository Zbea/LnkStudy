package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ParentHomeworkBean implements Serializable {
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
    public String pageStr;
    public int subject;
    public long time;
    @SerializedName("submitStatus")
    public int status;
}
