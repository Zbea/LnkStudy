package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import java.util.List;

public class AppBean {

    public int appId;
    public String appName;
    public String packageName;
    public Drawable image;
    public boolean isCheck;
    public boolean isBase;//基本数据

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

        public String[] images = {
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710"
                , "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg"
                ,"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-11%2F13%2Fa7590e12-844e-482c-aeb7-f06a8b248c6b%2Fa7590e12-844e-482c-aeb7-f06a8b248c6b1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659771383&t=800602d745210c44e69f6f4e274f30b5"
                , "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.mianfeiwendang.com%2Fpic%2F10cdaced536a7b129266bf36f167f2acab5f4e19%2F1-1242-png_6_0_0_135_211_606_892_892.979_1262.879-1005-0-0-1005.jpg&refer=http%3A%2F%2Fimg.mianfeiwendang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659756712&t=3625dadc24da52a151d9a93b41becc3f"
        };

        public int applicationId;
        public String contentUrl;

    }

}
