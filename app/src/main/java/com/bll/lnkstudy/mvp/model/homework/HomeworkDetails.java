package com.bll.lnkstudy.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeworkDetails {

    public List<HomeworkDetailBean> list;

    public class HomeworkDetailBean{
        public int studentTaskId;

        public String jobTitle;
        public String title;
        public long time;
        public long sendTime;
    }

}
