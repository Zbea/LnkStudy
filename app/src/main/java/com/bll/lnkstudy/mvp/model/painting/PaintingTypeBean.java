package com.bll.lnkstudy.mvp.model.painting;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaintingTypeBean implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int type;//类型0画本1书法
    public int grade;
    public long date;
    public boolean isCloud;
    public int cloudId;//云id
    @Generated(hash = 1248290185)
    public PaintingTypeBean(Long id, long userId, int type, int grade, long date, boolean isCloud,
            int cloudId) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.grade = grade;
        this.date = date;
        this.isCloud = isCloud;
        this.cloudId = cloudId;
    }
    @Generated(hash = 1478960882)
    public PaintingTypeBean() {
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
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public boolean getIsCloud() {
        return this.isCloud;
    }
    public void setIsCloud(boolean isCloud) {
        this.isCloud = isCloud;
    }
    public int getCloudId() {
        return this.cloudId;
    }
    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
    }
}
