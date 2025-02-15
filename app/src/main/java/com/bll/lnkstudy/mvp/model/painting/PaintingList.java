package com.bll.lnkstudy.mvp.model.painting;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 书画、壁纸 数据模型
 */
public class PaintingList {

    //app数据
    public int total;
    public List<ListBean> list;

    public static class ListBean {
        public int fontDrawId;
        public String drawName;
        public String imageUrl;
        public String bodyUrl;
        public String author;//作者
        public int supply;//1官方2第三方
        public int type;//1壁纸2书画
        public int dynasty;//1汉朝
        public int subType;//1毛笔书法
        @SerializedName("version")
        public String publisher;//出版社
        public int price;
        public String drawDesc;
        public int status;
        public int buyStatus;//0未购买1购买
    }

}
