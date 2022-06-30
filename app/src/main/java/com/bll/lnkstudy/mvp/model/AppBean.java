package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import java.util.List;

public class AppBean {

    public String appName;
    public String packageName;
    public Drawable image;

    //app数据
    public int pageIndex;
    public int pageSize;
    public int pageCount;
    public int totalCount;
    public List<ListBean> list;

    //app购买下载数据
    public int applicationId;
    public String contentUrl;

    public static class ListBean {
        /**
         * id : 60399099
         * name : Calculator
         * packageName :
         * status : 1
         * price : 0
         * assetUrl : https://assets.bailianlong.com/3508579b34bdf44158fcf5ed93a17062.png
         * introduction : 11
         */
        public int id;
        public String name;
        public String packageName;
        public int status;
        public int price;
        public String assetUrl;
        public String introduction;

        public int applicationId;
        public String contentUrl;

    }

}
