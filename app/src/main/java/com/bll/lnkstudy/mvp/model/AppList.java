package com.bll.lnkstudy.mvp.model;

import java.util.List;

public class AppList {

    //app数据
    public int total;
    public List<ListBean> list;

    public static class ListBean {
        public int applicationId;
        public String contentUrl;
        public String assetUrl;
        public String introduction;
        public String nickname;
        public String packageName;
        public int price;
        public int status;
        public int type;
        public String version;
        public int visible;
        public int buyStatus;
    }
}
