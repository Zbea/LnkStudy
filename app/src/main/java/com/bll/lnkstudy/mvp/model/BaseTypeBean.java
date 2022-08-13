package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

//分类列表
@Entity
public class BaseTypeBean {
    @Id(autoincrement = true)
    public Long id;
    public int typeId;//分类id
    public String name;
    @Transient
    public boolean isCheck;
    @Generated(hash = 2038045697)
    public BaseTypeBean(Long id, int typeId, String name) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
    }
    @Generated(hash = 108103880)
    public BaseTypeBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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

}
