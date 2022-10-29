package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WallpaperBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public int contentId;//内容id
    public int type;//0壁纸1书画

    public int time;//书画年代
    public String timeStr;
    public int paintingType;//书画类别
    public String paintingTypeStr;

    public String title;
    public String info;
    public int price;
    public String imageUrl;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;//图片保存地址
    public long date;
    @Transient
    public boolean isLeft;
    @Transient
    public boolean isRight;

    @Generated(hash = 1974190347)
    public WallpaperBean(Long id, long userId, int contentId, int type, int time,
            String timeStr, int paintingType, String paintingTypeStr, String title,
            String info, int price, String imageUrl, List<String> paths,
            long date) {
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
        this.paths = paths;
        this.date = date;
    }
    @Generated(hash = 915358704)
    public WallpaperBean() {
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
