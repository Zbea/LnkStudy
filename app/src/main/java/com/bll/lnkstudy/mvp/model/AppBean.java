package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import org.greenrobot.greendao.annotation.Generated;

import java.sql.Blob;

@Entity
public class AppBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String appName;
    @Unique
    public String packageName;
    public byte[] imageByte;
    @Transient
    public boolean isCheck;
    @Transient
    public boolean isBase;//基本数据
    @Generated(hash = 1499838802)
    public AppBean(Long id, long userId, String appName, String packageName,
            byte[] imageByte) {
        this.id = id;
        this.userId = userId;
        this.appName = appName;
        this.packageName = packageName;
        this.imageByte = imageByte;
    }
    @Generated(hash = 285800313)
    public AppBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public byte[] getImageByte() {
        return this.imageByte;
    }
    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }

}
