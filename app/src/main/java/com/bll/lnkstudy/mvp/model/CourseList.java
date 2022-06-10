package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

//科目列表
@Entity
public class CourseList {

    @Id(autoincrement = true)
    public Long id;
    @Unique
    public int viewId;//对应textview  ID
    public String name;//科目名称
    @Transient
    public Boolean isSelect=false;//是否存在加错
    @Transient
    public int imageId;//图片资源id


    @Generated(hash = 662460629)
    public CourseList(Long id, int viewId, String name) {
        this.id = id;
        this.viewId = viewId;
        this.name = name;
    }
    @Generated(hash = 2083424351)
    public CourseList() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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

}
