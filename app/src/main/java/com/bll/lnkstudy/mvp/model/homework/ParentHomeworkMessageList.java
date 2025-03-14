package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ParentHomeworkMessageList implements Serializable {

    public int total;
    public List<ParentMessageBean> list;

    public static class ParentMessageBean implements Serializable {
        @SerializedName("id")
        public int contendId;
        public String submitUrl;
        public String changeUrl;
        public int type;
        @SerializedName("parentHomeworkId")
        public int typeId;
        public String title;
        public String createTime;
        public int minute;
        public long endTime;
        public long submitTime;
        @SerializedName("name")
        public String homeworkName;
        public String pageStr;
        public int subject;
        public long time;
        @SerializedName("submitStatus")
        public int status;//1未提交2已提交3已批改
    }

}
