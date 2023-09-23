package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Note implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String title;
    public String typeStr;//分类
    public long date; //创建时间
    public String contentResId; //笔记内容背景id
    public boolean isCancelPassword;//取消加密
    public int grade;//年级
    public boolean isCloud;
    public int cloudId;//云id
    @Transient
    public String downloadUrl;
    @Transient
    public String contentJson;
    @Transient
    public boolean isSet;

    @Generated(hash = 1977792127)
    public Note(Long id, long userId, String title, String typeStr, long date,
            String contentResId, boolean isCancelPassword, int grade,
            boolean isCloud, int cloudId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.typeStr = typeStr;
        this.date = date;
        this.contentResId = contentResId;
        this.isCancelPassword = isCancelPassword;
        this.grade = grade;
        this.isCloud = isCloud;
        this.cloudId = cloudId;
    }
    @Generated(hash = 1272611929)
    public Note() {
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getContentResId() {
        return this.contentResId;
    }
    public void setContentResId(String contentResId) {
        this.contentResId = contentResId;
    }
    public boolean getIsCancelPassword() {
        return this.isCancelPassword;
    }
    public void setIsCancelPassword(boolean isCancelPassword) {
        this.isCancelPassword = isCancelPassword;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public boolean getIsCloud() {
        return this.isCloud;
    }
    public void setIsCloud(boolean isCloud) {
        this.isCloud = isCloud;
    }
    public int getCloudId() {
        return this.cloudId;
    }
    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
    }

}
