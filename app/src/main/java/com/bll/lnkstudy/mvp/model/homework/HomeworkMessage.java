package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeworkMessage implements Serializable {

    public int id;//作业本id
    public int total;
    public List<MessageBean> list=new ArrayList<>();

    public static class MessageBean implements Serializable{
        public int id;
        public String title;
        @SerializedName("name")
        public String typeName;
        public long endTime;
        public int submitState;
        public String subject;
        public int grade;
        public int status;//3未提交 1已提交 2已完成
        public int studentTaskId;
        public boolean isSelector;
    }

}
