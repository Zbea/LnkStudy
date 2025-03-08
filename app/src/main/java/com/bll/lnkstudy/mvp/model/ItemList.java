package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.List;

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
    public boolean isEdit;//目录可以修改
    public boolean isAdd;//提交选中页码

    public boolean isSelfCorrect;//是否自批
    public int typeId;
    public int submitState;//0提交1不提交
    public String question;
    public int questionType;//-1未加入模板0空模板
    public int questionMode;//1打分
    public String answerUrl;//答案

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
}
