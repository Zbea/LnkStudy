package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 获取作业卷（老师下发作业列表）
 */
public class HomeworkPaperList implements Serializable {

    public List<HomeworkPaperListBean> list;

    public static class HomeworkPaperListBean {
        @SerializedName("studentTaskId")
        public int contendId;
        public String title;//作业标题
        public String subject;//科目
        @SerializedName("commonTypeId")
        public int typeId;//收到作业卷分类id
        @SerializedName("examName")
        public String typeName;//收到作业卷分类名称
        @SerializedName("addType")
        public int autoState;//创建状态:1默认创建
        public int grade;
        public int subType;//2普通作业本 3听读本 1题卷本6写字本
        public String submitUrl;
        @SerializedName("taskImageId")
        public String imageUrl;
        public String page;
        public String path;//文件夹路径
        public String[] paths;//图片路径
        public long date;
        public long endTime;//结束时间
        public int status;//是否已经提交状态3学生未提交1已提交未批改2已批改
        public int sendStatus;//1老师下发2老师批改下发
        public double score;//批改成绩
        public int questionMode;//打分模式
        public int questionType;//批改模型
        public String question;//批改详情
        public String answerUrl;
        public int selfBatchStatus;
    }
}
