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
        public int subType;
        public String createTime;
        public int minute;//标准时间
        public long endTime;
        public int submitState;//0提交 1不提交
        public String subject;
        public int grade;
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
