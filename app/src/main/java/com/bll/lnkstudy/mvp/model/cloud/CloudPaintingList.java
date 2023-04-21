package com.bll.lnkstudy.mvp.model.cloud;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class CloudPaintingList {

    public int total;
    public List<PaintingListBean> list;

    public class PaintingListBean implements MultiItemEntity {
        public int id;
        public String name;
        public String imageUrl;
        public String bodyUrl;
        public String author;//作者
        public int supply;//1官方2第三方
        public int dynasty;//1汉朝
        public String dynastyStr;//1汉朝
        public int subType;//1毛笔书法
        public String subTypeStr;//1毛笔书法
        public String version;
        public int price;
        public String drawDesc;
        public long date;//下载时间
        public int type;//0书法1本地书画
        public int localType;//0画本1书法
        public int grade;

        @Override
        public int getItemType() {
            return type;
        }
    }

}
