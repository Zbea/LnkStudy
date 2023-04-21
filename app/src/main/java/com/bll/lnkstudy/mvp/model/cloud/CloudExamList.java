package com.bll.lnkstudy.mvp.model.cloud;

import java.util.List;

public class CloudExamList {

    public int total;
    public List<CloudExamTypeBean> list;

    public class CloudExamTypeBean {
        public int id;
        public String name;
        public String course;
        public int grade;//年级
        public long date; //创建时间
    }
}
