package com.bll.lnkstudy.mvp.model.note;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class NoteContentBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    public String typeStr;//分类
    public String noteTitle;
    public long date;//创建时间
    public String title;
    public String resId;//背景id
    public String filePath;//文件路径
    public int page;//页码
    public int grade;//年级
    @Generated(hash = 1594347445)
    public NoteContentBean(Long id, long userId, String typeStr, String noteTitle,
            long date, String title, String resId, String filePath, int page,
            int grade) {
        this.id = id;
        this.userId = userId;
        this.typeStr = typeStr;
        this.noteTitle = noteTitle;
        this.date = date;
        this.title = title;
        this.resId = resId;
        this.filePath = filePath;
        this.page = page;
        this.grade = grade;
    }
    @Generated(hash = 112846212)
    public NoteContentBean() {
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
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    public String getNoteTitle() {
        return this.noteTitle;
    }
    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
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
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }

    
}
