package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import java.util.List;

public class AppListBean {

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
        public String assetUrl="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201601%2F06%2F20160106155357_LmN2c.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1669341811&t=91051f3ca53a74f47cfc51cc4c61fe96";
        public String introduction;
        public String imageUrl="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fa%2F57baa85e3eb1d.jpg&refer=http%3A%2F%2Fpic1.win4000.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1669270103&t=4ca86cda9cc5fbdad9ffd03734b4721c";
        public String[] images = {"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.1ppt.com%2Fuploads%2Fallimg%2F2101%2F1_210113133659_3.JPG&refer=http%3A%2F%2Fimg.1ppt.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1669340950&t=3d20767139baab4422b962ba14a39998"
                , "https://img0.baidu.com/it/u=3955387438,502755987&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500"
        };

        //书画分类
        public int time;//书画年代
        public String timeStr;
        public int paintingType;//书画类别
        public String paintingTypeStr;

        public int applicationId;
        public String contentUrl;

    }

}
