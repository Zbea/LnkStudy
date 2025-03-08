package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 老师作业本消息
 */
public class HomeworkMessageList implements Serializable {

    public int total;
    public List<MessageBean> list;

    public static class MessageBean implements Serializable{
        public String title;
        @SerializedName("name")
        public String typeName;
        @SerializedName("commonTypeId")
        public int typeId;
        public String createTime;
        public long endTime;
        public int submitState;//0提交 1不提交
        public String subject;
        public int grade;
        public int status;//3未提交 1已提交 2已完成
        @SerializedName("studentTaskId")
        public int contendId;
        public String examUrl;
        public String question;
        public int questionType;//-1未加入模板0空模板
        public int questionMode;//1打分
        public int selfBatchStatus;//1自批
        public String answerUrl;//答案
    }
}
