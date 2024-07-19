package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;
import java.util.List;

public class TeachingVideoList {

    public int total;
    public List<VideoBean> list;

    public static class VideoBean implements Serializable {
        public int id;
        public int type;
        public String imageUrl;
        public String bodyUrl;
        public String info;
        public int grade;
        public int semester;
        public String videoName;
        public String videoDesc;
        public int status;
    }

}
