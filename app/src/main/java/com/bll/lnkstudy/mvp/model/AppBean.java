package com.bll.lnkstudy.mvp.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bll.lnkstudy.mvp.model.date.DateEventBean;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import org.greenrobot.greendao.annotation.Generated;

import java.sql.Blob;
import java.util.Objects;

@Entity
public class AppBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String appName;
    public String packageName;
    public byte[] imageByte;
    public boolean isTool;
    @Transient
    public boolean isCheck=false;

    @Generated(hash = 867208658)
    public AppBean(Long id, long userId, String appName, String packageName, byte[] imageByte,
            boolean isTool) {
        this.id = id;
        this.userId = userId;
        this.appName = appName;
        this.packageName = packageName;
        this.imageByte = imageByte;
        this.isTool = isTool;
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
    public boolean getIsTool() {
        return this.isTool;
    }
    public void setIsTool(boolean isTool) {
        this.isTool = isTool;
    }

}
