package com.bll.lnkstudy.mvp.model.cloud;

import java.util.List;

public class CloudHomeworkList {

    public int total;
    public List<CloudHomeworkTypeBean> list;

    public class CloudHomeworkTypeBean {
        public int id;//作业本分类id
        public String name;
        public String course;
        public int grade;//年级
        public int subType;//2普通作业本 3听读本 1题卷本
        public long date; //创建时间
        public String contentResId; //作业本内容背景id
        public String bgResId;//当前作业本背景样式id
    }
}
