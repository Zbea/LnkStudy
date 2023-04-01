package com.bll.lnkstudy.mvp.model.paper;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 本次考试
 */
@Entity
public class PaperBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    @Unique
    public int contentId;//这次考卷id
    public int type;//0作业1考卷
    public String course;//科目
    public int categoryId;//考卷分组id
    public String category;//考卷分组标题

    public int index;//（位置下标）
    public String title;

    public double score;
    public long createDate;//创建时间
    public long endTime;//老师需要学生提交时间
    public long commitDate;//提交时间
    public String path;//文件路径
    public int page;
    public boolean isPg=false;
    public boolean isCommit;//作业是否需要提交
    public int state;//提交状态3学生未提交1已提交未批改2已批改
    public String images;//下载地址
    @Generated(hash = 1583874389)
    public PaperBean(Long id, long userId, int contentId, int type, String course,
            int categoryId, String category, int index, String title, double score,
            long createDate, long endTime, long commitDate, String path, int page,
            boolean isPg, boolean isCommit, int state, String images) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.type = type;
        this.course = course;
        this.categoryId = categoryId;
        this.category = category;
        this.index = index;
        this.title = title;
        this.score = score;
        this.createDate = createDate;
        this.endTime = endTime;
        this.commitDate = commitDate;
        this.path = path;
        this.page = page;
        this.isPg = isPg;
        this.isCommit = isCommit;
        this.state = state;
        this.images = images;
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
    public int getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
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
    public double getScore() {
        return this.score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public long getEndTime() {
        return this.endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public long getCommitDate() {
        return this.commitDate;
    }
    public void setCommitDate(long commitDate) {
        this.commitDate = commitDate;
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
    public String getImages() {
        return this.images;
    }
    public void setImages(String images) {
        this.images = images;
    }

}
