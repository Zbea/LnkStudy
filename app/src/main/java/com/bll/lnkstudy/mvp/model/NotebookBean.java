package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class NotebookBean implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String title;
    public String typeStr;//分类
    public long createDate; //创建时间
    public String dateStr;
    public String contentResId; //笔记内容背景id
    public boolean isEncrypt;//是否加密
    public String encrypt;//密码
    public int grade;//年级
    public boolean isCloud;
    public int cloudId;//云id
    @Transient
    public String downloadUrl;
    @Transient
    public String contentJson;
    @Generated(hash = 1700255441)
    public NotebookBean(Long id, long userId, String title, String typeStr,
            long createDate, String dateStr, String contentResId, boolean isEncrypt,
            String encrypt, int grade, boolean isCloud, int cloudId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.typeStr = typeStr;
        this.createDate = createDate;
        this.dateStr = dateStr;
        this.contentResId = contentResId;
        this.isEncrypt = isEncrypt;
        this.encrypt = encrypt;
        this.grade = grade;
        this.isCloud = isCloud;
        this.cloudId = cloudId;
    }
    @Generated(hash = 1250124346)
    public NotebookBean() {
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
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public String getDateStr() {
        return this.dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    public String getContentResId() {
        return this.contentResId;
    }
    public void setContentResId(String contentResId) {
        this.contentResId = contentResId;
    }
    public boolean getIsEncrypt() {
        return this.isEncrypt;
    }
    public void setIsEncrypt(boolean isEncrypt) {
        this.isEncrypt = isEncrypt;
    }
    public String getEncrypt() {
        return this.encrypt;
    }
    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
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
