package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Note implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public int type;//分类
    public long notebookId;//笔记本id
    public long date;//创建时间
    public String title;
    public String resId;//背景id
    public String folderPath;//文件夹路径
    public String filePath;//文件路径
    public String pathName;//文件名
    public int page;//页码
    @Generated(hash = 1757216792)
    public Note(Long id, long userId, int type, long notebookId, long date,
            String title, String resId, String folderPath, String filePath,
            String pathName, int page) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.notebookId = notebookId;
        this.date = date;
        this.title = title;
        this.resId = resId;
        this.folderPath = folderPath;
        this.filePath = filePath;
        this.pathName = pathName;
        this.page = page;
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
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public long getNotebookId() {
        return this.notebookId;
    }
    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getResId() {
        return this.resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public String getFolderPath() {
        return this.folderPath;
    }
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getPathName() {
        return this.pathName;
    }
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }

  

}
