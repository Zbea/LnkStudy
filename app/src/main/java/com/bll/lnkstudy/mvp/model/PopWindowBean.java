package com.bll.lnkstudy.mvp.model;

public class PopWindowBean {

    public int id;
    public String name;
    public int resId;
    public boolean isCheck;

    public PopWindowBean() {
    }

    public PopWindowBean(int id, String name, boolean isCheck) {
        this.id = id;
        this.name = name;
        this.isCheck = isCheck;
    }

    public PopWindowBean(int id, String name, int resId) {
        this.id = id;
        this.name = name;
        this.resId = resId;
    }
}
