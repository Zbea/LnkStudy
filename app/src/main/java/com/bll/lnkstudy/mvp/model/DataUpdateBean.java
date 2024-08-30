package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

/**
 * 数据增量更新 1课本2作业3考卷4笔记5画本6书架7题卷本8日记
 *  type为1时：1内容2手写
 *  type为2时：1作业分类2作业内容3本次作业卷内容；
 *  type为3时：1考卷分类2考试3本次考试内容；
 *  type为4时：1笔记分类2主题3笔记内容
 *  type为5时：1分类2内容
 *  type为6时：1内容2手写
 *  type为7时：1手写 2题卷批改信息
 */
@Entity
public class DataUpdateBean {

    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int uid;//标识
    public int type;//类型1课本2作业3考卷4笔记5画本6书架7题卷本
    public int typeId;//用来防止重复
    public int contentType;//内容分类
    public int state;//type==2时：1作业卷 2普通作业本 3听读本
    public String listJson;
    public long date;
    public String downloadUrl;//上传文件下载地址
    public String path;//本地上传文件路径
    public boolean isDelete;//是否清除后台数据
    public boolean isUpload;//是否已经上传

    @Generated(hash = 41731880)
    public DataUpdateBean(Long id, long userId, int uid, int type, int typeId, int contentType,
            int state, String listJson, long date, String downloadUrl, String path, boolean isDelete,
            boolean isUpload) {
        this.id = id;
        this.userId = userId;
        this.uid = uid;
        this.type = type;
        this.typeId = typeId;
        this.contentType = contentType;
        this.state = state;
        this.listJson = listJson;
        this.date = date;
        this.downloadUrl = downloadUrl;
        this.path = path;
        this.isDelete = isDelete;
        this.isUpload = isUpload;
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
    public int getUid() {
        return this.uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getContentType() {
        return this.contentType;
    }
    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
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
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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
    public boolean getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }


}
