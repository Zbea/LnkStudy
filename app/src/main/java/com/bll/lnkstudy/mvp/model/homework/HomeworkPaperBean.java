package com.bll.lnkstudy.mvp.model.homework;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

/**
 * 作业卷
 */
@Entity
public class HomeworkPaperBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    @Unique
    public int contentId;//这次作业id
    public String course;//科目
    public int typeId;//作业分组id
    public String type;//作业分组标题
    public int index;//（位置下标）
    public String title;
    public long endTime;//老师需要学生提交时间
    public String path;//文件路径
    public int page;
    public boolean isPg=false;
    public boolean isCommit;//作业是否需要提交
    public int state;//提交状态3学生未提交1已提交未批改2已批改

    @Generated(hash = 527678266)
    public HomeworkPaperBean(Long id, long userId, int contentId, String course, int typeId,
            String type, int index, String title, long endTime, String path, int page, boolean isPg,
            boolean isCommit, int state) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.course = course;
        this.typeId = typeId;
        this.type = type;
        this.index = index;
        this.title = title;
        this.endTime = endTime;
        this.path = path;
        this.page = page;
        this.isPg = isPg;
        this.isCommit = isCommit;
        this.state = state;
    }
    @Generated(hash = 1573712411)
    public HomeworkPaperBean() {
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
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getEndTime() {
        return this.endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
    public boolean getIsPg() {
        return this.isPg;
    }
    public void setIsPg(boolean isPg) {
        this.isPg = isPg;
    }
    public boolean getIsCommit() {
        return this.isCommit;
    }
    public void setIsCommit(boolean isCommit) {
        this.isCommit = isCommit;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    
}
