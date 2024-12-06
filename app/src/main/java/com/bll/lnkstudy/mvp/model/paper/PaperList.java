package com.bll.lnkstudy.mvp.model.paper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaperList implements Serializable {

    public int total;
    public List<PaperListBean> list;

    public static class PaperListBean implements Serializable {
        @SerializedName("studentTaskId")
        public int id;
        public String title;//作业标题
        public String subject;//科目
        @SerializedName("addType")
        public int autoState;//创建状态：1默认创建
        @SerializedName("commonTypeId")
        public int typeId;//收到考卷 分类id
        @SerializedName("examName")
        public String typeName;//收到考卷 分类名称
        public int grade;
        public String submitUrl;
        public String studentUrl;
        public String path;//文件夹路径
        public List<String> paths;//图片路径
        public long date;
        public long endTime;
        public double score;
        public int questionType;//批改模型
        public String question;//批改详情
        public String answerUrl;
        public int questionMode;
    }

}
