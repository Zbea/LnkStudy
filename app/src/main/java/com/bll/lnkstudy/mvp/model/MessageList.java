package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageList {
    public int total;
    public List<MessageBean> list;

    public class MessageBean {

        public int id;
        public String teacherName;
        @SerializedName("title")
        public String content;
        public long date;
        public int sendType;

    }
}
