package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemList implements Serializable,Comparable<ItemList> {

    public int type;
    public String desc;
    public boolean isCheck;

    public int id;
    public String name;
    public int page;//目录页码
    public String date;
    public Drawable icon;
    public Drawable icon_check;
    public String info;
    public String url;
    public int resId;
    public boolean isDelete;//目录可以删除
    public boolean isEdit;//目录可以修改
    public boolean isAdd;//提交选中页码

    public ItemList() {
    }

    public ItemList(int id,int type, String desc) {
        this.id=id;
        this.type = type;
        this.desc = desc;
    }


    public ItemList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(ItemList itemList) {
        return this.id-itemList.id;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemList item = (ItemList) obj;
        return Objects.equals(desc, item.desc) && type==item.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(desc, type);
    }


}
