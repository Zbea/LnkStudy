package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaperContent {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public int userId;//用户id
    public int type;//0作业1考卷
    public int courseId;//科目id
    public int categoryId;//考卷分组id
    public long paperId;//考卷id
    public long date;
    public String path;//原图路径
    public String drawPath;//绘图路径
    public int page;//页码
    @Generated(hash = 1606168291)
    public PaperContent(Long id, int userId, int type, int courseId, int categoryId,
            long paperId, long date, String path, String drawPath, int page) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.paperId = paperId;
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
    public int getUserId() {
        return this.userId;
    }
    public void setUserId(int userId) {
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
    public long getPaperId() {
        return this.paperId;
    }
    public void setPaperId(long paperId) {
        this.paperId = paperId;
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
