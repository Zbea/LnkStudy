package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

//分类列表
@Entity
public class NoteTypeBean {

    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public int typeId;//分类id
    public String name;
    public long date;

    @Generated(hash = 391098658)
    public NoteTypeBean(Long id, long userId, int typeId, String name, long date) {
        this.id = id;
        this.userId = userId;
        this.typeId = typeId;
        this.name = name;
        this.date = date;
    }
    @Generated(hash = 1520090161)
    public NoteTypeBean() {
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
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    
}
