package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

public class MessageBean {

    public int id;
    public String teacherName;
    @SerializedName("title")
    public String content;
    public long date;
    public int sendType;

}
