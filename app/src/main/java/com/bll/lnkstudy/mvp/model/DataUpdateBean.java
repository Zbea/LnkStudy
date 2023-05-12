package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

/**
 * 数据增量更新
 */
@Entity
public class DataUpdateBean {

    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int uid;
    public int type;//类型0书架1课本2作业3考卷4笔记5画本
    public int contentType;//0分类1内容2目录
    public String listJson;
    public long date;
    public String downloadUrl;//上传文件地址
    public String sourceUrl;//源文件地址
    public String path;//本地上传文件路径
    public boolean isDelete;//是否清除后台数据

    @Generated(hash = 556112973)
    public DataUpdateBean(Long id, long userId, int uid, int type, int contentType, String listJson,
            long date, String downloadUrl, String sourceUrl, String path, boolean isDelete) {
        this.id = id;
        this.userId = userId;
        this.uid = uid;
        this.type = type;
        this.contentType = contentType;
        this.listJson = listJson;
        this.date = date;
        this.downloadUrl = downloadUrl;
        this.sourceUrl = sourceUrl;
        this.path = path;
        this.isDelete = isDelete;
    }
    @Generated(hash = 170642699)
    public DataUpdateBean() {
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
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getContentType() {
        return this.contentType;
    }
    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
    public String getListJson() {
        return this.listJson;
    }
    public void setListJson(String listJson) {
        this.listJson = listJson;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public int getUid() {
        return this.uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getSourceUrl() {
        return this.sourceUrl;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public boolean getIsDelete() {
        return this.isDelete;
    }
    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }


}
