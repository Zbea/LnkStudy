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
        public int count;
        public int createdAt;
        public String introduction;
        public String nickname;
        public String packageName;
        public int price;
        public int publishedAt;
        public int status;
        public int type;
        public String version;
        public int visible;
        public int buyStatus;

        //书画分类
        public int time;//书画年代
        public String timeStr;
        public int paintingType;//书画类别
        public String paintingTypeStr;

        public String imageUrl="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fa%2F57baa85e3eb1d.jpg&refer=http%3A%2F%2Fpic1.win4000.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1669270103&t=4ca86cda9cc5fbdad9ffd03734b4721c";
        public String[] images = {"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.1ppt.com%2Fuploads%2Fallimg%2F2101%2F1_210113133659_3.JPG&refer=http%3A%2F%2Fimg.1ppt.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1669340950&t=3d20767139baab4422b962ba14a39998"
                , "https://img0.baidu.com/it/u=3955387438,502755987&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500"
        };


    }

}
