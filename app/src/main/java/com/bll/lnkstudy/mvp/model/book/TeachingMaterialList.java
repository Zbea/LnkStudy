package com.bll.lnkstudy.mvp.model.book;

import java.util.List;

public class TeachingMaterialList {

    public int total;
    public List<TeachingMaterialBean> list;

    public static class TeachingMaterialBean {
        public int id;
        public String title;
        public String createTime;
        public String teacherName;
        public int grade;
        public int subject;
        public String url;
    }
}
