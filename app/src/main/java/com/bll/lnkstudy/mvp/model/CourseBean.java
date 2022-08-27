package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

//科目列表
@Entity
public class CourseBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    @Unique
    public int viewId;//对应textview  ID
    public String name;//科目名称
    public int type;//五天六节课类型
    @Transient
    public Boolean isSelect=false;//是否存在加错
    @Transient
    public int imageId;//图片资源id
    @Transient
    public int courseId;//科目id
    @Transient
    public int mainCourseId;//首页课业进度背景图

    @Generated(hash = 1270664387)
    public CourseBean(Long id, long userId, int viewId, String name, int type) {
        this.id = id;
        this.userId = userId;
        this.viewId = viewId;
        this.name = name;
        this.type = type;
    }

    @Generated(hash = 858107730)
    public CourseBean() {
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
    public int getViewId() {
        return this.viewId;
    }
    public void setViewId(int viewId) {
        this.viewId = viewId;
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

}
