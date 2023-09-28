package com.bll.lnkstudy.mvp.model.painting;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;


@Entity
public class PaintingDrawingBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int type;//类型
    public int bgResId;
    public long date;//开始时间
    public String path;//图片路径
    public String title;
    public int page;
    public int grade;
    @Generated(hash = 228816449)
    public PaintingDrawingBean(Long id, long userId, int type, int bgResId, long date, String path,
            String title, int page, int grade) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.bgResId = bgResId;
        this.date = date;
        this.path = path;
        this.title = title;
        this.page = page;
        this.grade = grade;
    }
    @Generated(hash = 1527197434)
    public PaintingDrawingBean() {
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
    public int getBgResId() {
        return this.bgResId;
    }
    public void setBgResId(int bgResId) {
        this.bgResId = bgResId;
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
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
