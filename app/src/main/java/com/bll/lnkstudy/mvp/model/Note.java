package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.greendao.DateRemindConverter;
import com.bll.lnkstudy.utils.greendao.NoteConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Note implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    @Unique
    public long date;//创建时间
    public long nowDate;//最近查看时间
    public String title;
    public int type;//分类
    public int resId;//原图id
    public int index=0;//当前文件名的最大值
    public String path;//文件夹地址
    @Convert(columnType = String.class,converter = NoteConverter.class)
    public List<String> paths;//生成图片路径

    @Generated(hash = 538885526)
    public Note(Long id, long date, long nowDate, String title, int type, int resId,
            int index, String path, List<String> paths) {
        this.id = id;
        this.date = date;
        this.nowDate = nowDate;
        this.title = title;
        this.type = type;
        this.resId = resId;
        this.index = index;
        this.path = path;
        this.paths = paths;
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
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public long getNowDate() {
        return this.nowDate;
    }
    public void setNowDate(long nowDate) {
        this.nowDate = nowDate;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getResId() {
        return this.resId;
    }
    public void setResId(int resId) {
        this.resId = resId;
    }
    public List<String> getPaths() {
        return this.paths;
    }
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }


}
