package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
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
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    @Unique
    public int contentId;//这次考卷id
    public int type;//0作业1考卷
    public String course;//科目
    public int categoryId;//考卷分组id
    public String category;//考卷分组标题

    public int index;//（位置下标）
    public String title;
    public int rank;
    public double score;
    public long createDate;//创建时间
    public long date;//批改时间
    public String path;//文件路径
    public int page;
    public boolean isPg=false;

    public String images;//下载地址

    @Generated(hash = 1392193337)
    public PaperBean(Long id, long userId, int contentId, int type, String course,
            int categoryId, String category, int index, String title, int rank,
            double score, long createDate, long date, String path, int page,
            boolean isPg, String images) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.type = type;
        this.course = course;
        this.categoryId = categoryId;
        this.category = category;
        this.index = index;
        this.title = title;
        this.rank = rank;
        this.score = score;
        this.createDate = createDate;
        this.date = date;
        this.path = path;
        this.page = page;
        this.isPg = isPg;
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

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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

    public String getImages() {
        return this.images;
    }

    public void setImages(String images) {
        this.images = images;
    }


}