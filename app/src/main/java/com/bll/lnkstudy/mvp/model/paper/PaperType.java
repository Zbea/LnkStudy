package com.bll.lnkstudy.mvp.model.paper;

import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

public class PaperType {
    public int total;
    public List<PaperTypeBean> list;

    public class PaperTypeBean{
        public int id;
        public int userId;//老师id
        public String name;//考卷分类
        public String course;
        public int grade;
        @Transient
        public boolean isPg;//是否批改
        @Transient
        public int score;
    }

}
