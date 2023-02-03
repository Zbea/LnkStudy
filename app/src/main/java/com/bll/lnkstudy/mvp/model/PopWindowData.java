package com.bll.lnkstudy.mvp.model;

public class PopWindowData {

    public int id;
    public String name;
    public int resId;
    public boolean isCheck;

    public PopWindowData() {
    }

    public PopWindowData(int id, String name, boolean isCheck) {
        this.id = id;
        this.name = name;
        this.isCheck = isCheck;
    }

    public PopWindowData(int id, String name, int resId) {
        this.id = id;
        this.name = name;
        this.resId = resId;
    }
}
