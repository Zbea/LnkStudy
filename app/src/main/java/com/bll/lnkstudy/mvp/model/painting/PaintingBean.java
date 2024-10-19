package com.bll.lnkstudy.mvp.model.painting;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 书画以及壁纸 本地存储
 */
@Entity
public class PaintingBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public int contentId;//内容id
    public int type;//1壁纸2书画

    public int time;//书画年代
    public String timeStr;
    public int paintingType;//书画类别
    public String paintingTypeStr;

    public String title;
    public String info;
    public int price;
    public String imageUrl;
    public String bodyUrl;
    public int supply;//1官方2第三方
    public String author;//作者
    public String publisher;//出版社

    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;//图片保存地址
    public long date;//下载时间
    @Transient
    public boolean isLeft;
    @Transient
    public boolean isRight;

    @Generated(hash = 1227138445)
    public PaintingBean(Long id, long userId, int contentId, int type, int time, String timeStr,
            int paintingType, String paintingTypeStr, String title, String info, int price,
            String imageUrl, String bodyUrl, int supply, String author, String publisher,
            List<String> paths, long date) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.type = type;
        this.time = time;
        this.timeStr = timeStr;
        this.paintingType = paintingType;
        this.paintingTypeStr = paintingTypeStr;
        this.title = title;
        this.info = info;
        this.price = price;
        this.imageUrl = imageUrl;
        this.bodyUrl = bodyUrl;
        this.supply = supply;
        this.author = author;
        this.publisher = publisher;
        this.paths = paths;
        this.date = date;
    }
    @Generated(hash = 1284832375)
    public PaintingBean() {
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
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public String getTimeStr() {
        return this.timeStr;
    }
    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
    public int getPaintingType() {
        return this.paintingType;
    }
    public void setPaintingType(int paintingType) {
        this.paintingType = paintingType;
    }
    public String getPaintingTypeStr() {
        return this.paintingTypeStr;
    }
    public void setPaintingTypeStr(String paintingTypeStr) {
        this.paintingTypeStr = paintingTypeStr;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getInfo() {
        return this.info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getBodyUrl() {
        return this.bodyUrl;
    }
    public void setBodyUrl(String bodyUrl) {
        this.bodyUrl = bodyUrl;
    }
    public int getSupply() {
        return this.supply;
    }
    public void setSupply(int supply) {
        this.supply = supply;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublisher() {
        return this.publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public List<String> getPaths() {
        return this.paths;
    }
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }


}
