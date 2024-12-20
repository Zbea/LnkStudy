package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ParentHomeworkMessageList implements Serializable {

    public int total;
    public List<ParentHomeworkBean> list;

    public static class ParentHomeworkBean implements Serializable {
        public int id;
        public String submitUrl;
        public String changeUrl;
        public int type;
        @SerializedName("parentHomeworkId")
        public int typeId;
        public String title;
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

}
