package com.bll.lnkstudy.mvp.model.homework;

import androidx.annotation.Nullable;

import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeworkMessage implements Serializable {

    public int id;//作业本id
    public int total;
    public List<MessageBean> list;

    public static class MessageBean implements Serializable{
        public int id;
        public String title;
        @SerializedName("name")
        public String typeName;
        public long endTime;
        public int submitState;
        public String subject;
        public int grade;
        public int status;//3未提交 1已提交 2已完成
        public int studentTaskId;
        public boolean isSelector;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj==null)
                return false;
            if (!(obj instanceof MessageBean))
                return false;
            if (this==obj)
                return true;
            MessageBean item=(MessageBean) obj;
            return this.id==item.id&& Objects.equals(this.title, item.title) && Objects.equals(this.typeName, item.typeName)
                    &&this.endTime==item.endTime&&Objects.equals(this.subject, item.subject)
                    &&this.status==item.status&&this.studentTaskId==item.studentTaskId;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof HomeworkMessage))
            return false;
        if (this==obj)
            return true;
        HomeworkMessage item=(HomeworkMessage) obj;
        return this.id==item.id &&this.total==item.total && list==item.list;
    }
}
