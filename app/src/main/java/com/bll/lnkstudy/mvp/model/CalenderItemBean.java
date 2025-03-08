package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.MethodManager;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

@Entity
public class CalenderItemBean {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= MethodManager.getAccountId();
    public int pid;
    public String imageUrl;
    public String downloadUrl;
    public String previewUrl;
    public String title;
    public String introduction;
    public int buyStatus;
    public long time;//上架时间
    public int price;
    public long date;//下载时间
    @SerializedName("years")
    public int year;
    public boolean isSet;
    public String path;
    @Transient
    public int loadSate;
    @Generated(hash = 205828730)
    public CalenderItemBean(Long id, long userId, int pid, String imageUrl, String downloadUrl,
            String previewUrl, String title, String introduction, int buyStatus, long time, int price,
            long date, int year, boolean isSet, String path) {
        this.id = id;
        this.userId = userId;
        this.pid = pid;
        this.imageUrl = imageUrl;
        this.downloadUrl = downloadUrl;
        this.previewUrl = previewUrl;
        this.title = title;
        this.introduction = introduction;
        this.buyStatus = buyStatus;
        this.time = time;
        this.price = price;
        this.date = date;
        this.year = year;
        this.isSet = isSet;
        this.path = path;
    }
    @Generated(hash = 880114066)
    public CalenderItemBean() {
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
    public int getPid() {
        return this.pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getPreviewUrl() {
        return this.previewUrl;
    }
    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getIntroduction() {
        return this.introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public int getBuyStatus() {
        return this.buyStatus;
    }
    public void setBuyStatus(int buyStatus) {
        this.buyStatus = buyStatus;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public int getYear() {
        return this.year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public boolean getIsSet() {
        return this.isSet;
    }
    public void setIsSet(boolean isSet) {
        this.isSet = isSet;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    
}
