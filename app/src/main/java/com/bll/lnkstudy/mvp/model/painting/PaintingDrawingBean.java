package com.bll.lnkstudy.mvp.model.painting;

import com.bll.lnkstudy.MethodManager;
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
    public long userId= MethodManager.getAccountId();
    public int type;//类型
    public long date;//开始时间
    public String path;//图片路径
    public String title;
    public String bgRes;
    public int cloudId;

    @Generated(hash = 474092944)
    public PaintingDrawingBean(Long id, long userId, int type, long date,
            String path, String title, String bgRes, int cloudId) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.date = date;
        this.path = path;
        this.title = title;
        this.bgRes = bgRes;
        this.cloudId = cloudId;
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
    public String getBgRes() {
        return this.bgRes;
    }
    public void setBgRes(String bgRes) {
        this.bgRes = bgRes;
    }
    public int getCloudId() {
        return this.cloudId;
    }
    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
    }
    
}
