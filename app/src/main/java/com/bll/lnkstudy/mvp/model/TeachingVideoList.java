package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;
import java.util.List;

public class TeachingVideoList {

    public int total;
    public List<ItemBean> list;

    public class ItemBean implements Serializable {
        public int id;
        public int type;
        public String imageUrl;
        public String bodyUrl;
        public String info;
        public int grade;
        public int semester;
        public String videoName;
        public String videoDesc;
        public String videoVersion;
        public String createTime;
        public int status;
    }

}
