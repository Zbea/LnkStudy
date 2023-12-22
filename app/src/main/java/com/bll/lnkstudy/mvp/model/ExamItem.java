package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExamItem {
    public ExamBean exam;
    public int type;
    public static class ExamBean implements Serializable {
        @SerializedName("studentTaskId")
        public int id;
        public int userId;
        public String createTime;
        public int classId;
        public String submitUrl;
        public int taskId;
        public int status;
        public int score;
        public int sendStatus;
        public int examChangeId;
        public String studentUrl;
        public int deleteStatus;
        public int type;
        public int downloadStatus;
        public int submitTime;
        public int sendTime;
        public String page;
        public long endTime;
        public String name;
        public String className;
        @SerializedName("taskImageId")
        public String imageUrl;
        public String subject;
        public String examName;
        public int date;
        public String title;
        public String jobTitle;
        public int submitStatus;
        public int commonTypeId;
        public List<String> paths;//本地图片路径
    }
}
