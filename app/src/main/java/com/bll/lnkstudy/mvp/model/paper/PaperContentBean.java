package com.bll.lnkstudy.mvp.model.paper;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Map;
import java.util.Objects;

/**
 * 考卷试卷
 */
@Entity
public class PaperContentBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String course;
    public int typeId;//考卷分组id
    public int contentId;//考卷id
    public String path;//原图路径
    public String drawPath;//绘图路径
    public int page;//页码

    @Generated(hash = 879524499)
    public PaperContentBean(Long id, long userId, String course, int typeId,
            int contentId, String path, String drawPath, int page) {
        this.id = id;
        this.userId = userId;
        this.course = course;
        this.typeId = typeId;
        this.contentId = contentId;
        this.path = path;
        this.drawPath = drawPath;
        this.page = page;
    }
    @Generated(hash = 708894727)
    public PaperContentBean() {
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
    public String getCourse() {
        return this.course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
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
