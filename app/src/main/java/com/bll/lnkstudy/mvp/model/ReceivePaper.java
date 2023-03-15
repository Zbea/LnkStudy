package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReceivePaper implements Serializable {

    public int total;
    public List<PaperBean> list;

    public class PaperBean implements Serializable {
        @SerializedName("studentTaskId")
        public int id;
        public String title;//作业标题
        public String subject;//科目
        @SerializedName("taskId")
        public int examId;//收到考卷 分类id
        public String examName;//收到考卷 分类名称
        public int score;
        public String submitUrl;
        public String studentUrl;
        @SerializedName("taskImageId")
        public String imageUrl;
        public String path;//文件夹路径
        public String[] paths;//图片路径
        public long date;
    }

}
