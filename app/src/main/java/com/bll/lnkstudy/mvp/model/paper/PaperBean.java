package com.bll.lnkstudy.mvp.model.paper;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

/**
 * 本次考试
 */
@Entity
public class PaperBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int contentId;//这次考卷id
    public String course;//科目
    public int typeId;//考卷分组id
    public String type;//考卷分组标题
    public String title;
    public String path;//文件路径
    public int page;
    @Generated(hash = 1942965366)
    public PaperBean(Long id, long userId, int contentId, String course, int typeId, String type,
            String title, String path, int page) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.course = course;
        this.typeId = typeId;
        this.type = type;
        this.title = title;
        this.path = path;
        this.page = page;
    }
    @Generated(hash = 1608836968)
    public PaperBean() {
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
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
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
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
}
