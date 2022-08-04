package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;


@Entity
public class PaintingBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public int type;//类型
    public int bgResId;
    public long date;//开始时间
    public String path;//图片路径
    @Generated(hash = 787134327)
    public PaintingBean(Long id, int type, int bgResId, long date, String path) {
        this.id = id;
        this.type = type;
        this.bgResId = bgResId;
        this.date = date;
        this.path = path;
    }
    @Generated(hash = 1284832375)
    public PaintingBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getBgResId() {
        return this.bgResId;
    }
    public void setBgResId(int bgResId) {
        this.bgResId = bgResId;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }


}
