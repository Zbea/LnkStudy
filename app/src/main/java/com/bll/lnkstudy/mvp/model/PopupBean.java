package com.bll.lnkstudy.mvp.model;

public class PopupBean {

    public int id;
    public String name;
    public int resId;
    public boolean isCheck;

    public PopupBean() {
    }

    public PopupBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public PopupBean(int id, String name, boolean isCheck) {
        this.id = id;
        this.name = name;
        this.isCheck = isCheck;
    }

    public PopupBean(int id, String name, int resId) {
        this.id = id;
        this.name = name;
        this.resId = resId;
    }
}
