package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 获取作业卷（老师下发作业列表）
 */
public class HomeworkReel implements Serializable {

    @SerializedName("id")
    public int typeId;//作业分类ID
    public int subType;//作业分类
    public List<HomeworkReelBean> list;

    public class HomeworkReelBean implements Serializable{
        @SerializedName("studentTaskId")
        public int id;
        public String title;//作业标题
        public String subject;//科目
        @SerializedName("commonTypeId")
        public int typeId;//收到作业卷分类id
        @SerializedName("name")
        public String typeName;//收到作业卷分类名称
        public int subType;//2普通作业本 3听读本 1题卷本
        public String submitUrl;
        public String studentUrl;
        @SerializedName("taskImageId")
        public String imageUrl;
        public String path;//文件夹路径
        public String[] paths;//图片路径
        public long date;
        public long endTime;//结束时间
        public int submitStatus;//提交状态 1不提交 0 需要提交
        public int status;//是否已经提交状态3学生未提交1已提交未批改2已批改
        public int sendStatus;//1老师下发2老师批改下发
    }
}
