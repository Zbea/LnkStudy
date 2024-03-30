package com.bll.lnkstudy.mvp.model.paper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 学生考试
 */
public class ExamItem {
    public ExamBean exam;
    public int type;
    public static class ExamBean implements Serializable {
        @SerializedName("studentTaskId")
        public int id;
        public int userId;
        public int status;
        public int type;
        public String page;
        public long endTime;
        public String name;
        @SerializedName("taskImageId")
        public String imageUrl;
        public String subject;
        public String examName;
        public int date;
        public String title;
        public int commonTypeId;
        public List<String> paths;//本地图片路径
    }
}
