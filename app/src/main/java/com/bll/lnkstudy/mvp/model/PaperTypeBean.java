package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 考卷分类
 */
@Entity
public class PaperTypeBean  {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String name;//考卷分类
    public int type;//考卷分类id
    public String course;
    @Transient
    public boolean isPg;//是否批改
    @Transient
    public int score;

    public PaperTypeBean(String name, int type, boolean isPg) {
        this.name = name;
        this.type = type;
        this.isPg = isPg;
    }

    public PaperTypeBean() {
    }

    @Generated(hash = 519007326)
    public PaperTypeBean(Long id, long userId, String name, int type,
            String course) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.course = course;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCourse() {
        return this.course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
