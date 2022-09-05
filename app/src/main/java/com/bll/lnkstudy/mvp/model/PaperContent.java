package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaperContent {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public int type;//0作业1考卷
    public int courseId;//科目id
    public int categoryId;//考卷分组id
    public int contentId;//考卷id
    public long date;
    public String path;//原图路径
    public String drawPath;//绘图路径
    public int page;//页码
    @Generated(hash = 1920762037)
    public PaperContent(Long id, long userId, int type, int courseId,
            int categoryId, int contentId, long date, String path, String drawPath,
            int page) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.contentId = contentId;
        this.date = date;
        this.path = path;
        this.drawPath = drawPath;
        this.page = page;
    }
    @Generated(hash = 792024976)
    public PaperContent() {
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
    public int getCourseId() {
        return this.courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public int getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getDrawPath() {
        return this.drawPath;
    }
    public void setDrawPath(String drawPath) {
        this.drawPath = drawPath;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }



}
