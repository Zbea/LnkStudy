package com.bll.lnkstudy.mvp.model.homework;

import androidx.annotation.Nullable;

import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 老师作业本消息
 */
public class HomeworkMessage implements Serializable {

    public int total;
    public List<MessageBean> list;

    public static class MessageBean implements Serializable{
        public int id;
        public String title;
        @SerializedName("name")
        public String typeName;
        public long endTime;
        public String subject;
        public int grade;
        public int status;//3未提交 1已提交 2已完成
        public int studentTaskId;
        public String question;
        public int questionType;//-1未加入模板0空模板
        public int questionMode;//1打分
        public int selfBatchStatus;//1自批
        public String answerUrl;//答案
    }
}
