package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

public class ItemList implements Serializable,Comparable<ItemList> {

    public int type;
    public String desc;
    public boolean isCheck;

    public int id;
    public String name;
    public int page;//目录页码
    public String date;

    public String info;
    public String url;
    public String address="https://poss-videocloud.cns.com.cn/oss/2021/05/08/chinanews/MEIZI_YUNSHI/onair/25AFA3CA2F394DB38420CC0A44483E82.mp4";

    public ItemList() {
    }

    public ItemList(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    @Override
    public int compareTo(ItemList itemList) {
        return this.id-itemList.id;
    }
}
