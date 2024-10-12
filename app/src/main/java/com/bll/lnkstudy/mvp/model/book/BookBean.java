package com.bll.lnkstudy.mvp.model.book;

import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

/**
 * 书籍
 */
@Entity
public class BookBean {

    @Id(autoincrement = true)
    @Unique
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    @Unique
    @SerializedName("bookPlusId")
    public int bookId;
    public int typeId;//1古籍2自然科学3社会科学4思维科学5运动才艺
    public String subtypeStr;//子分类
    public String imageUrl;
    public String bookName;//书名
    public String bookDesc;//描述
    public int price;//书的价格
    public int grade; //年级
    public String version;  //版本
    public String supply ;  //官方
    @SerializedName("bodyUrl")
    public String downloadUrl;//书籍下载url
    public String bookPath;  //book书的路径
    public String bookDrawPath;  //book书的手写路径
    public long time;//观看时间
    public boolean isLook;//是否打开
    @Transient
    public String drawUrl;//云存储的手写下载地址
    @Transient
    public int loadSate;//0未下载 1正下载 2已下载
    @Transient
    public int buyStatus;//1已购买
    @Transient
    public int cloudId;

    @Generated(hash = 906519533)
    public BookBean(Long id, long userId, int bookId, int typeId, String subtypeStr, String imageUrl,
            String bookName, String bookDesc, int price, int grade, String version, String supply,
            String downloadUrl, String bookPath, String bookDrawPath, long time, boolean isLook) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.typeId = typeId;
        this.subtypeStr = subtypeStr;
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.bookDesc = bookDesc;
        this.price = price;
        this.grade = grade;
        this.version = version;
        this.supply = supply;
        this.downloadUrl = downloadUrl;
        this.bookPath = bookPath;
        this.bookDrawPath = bookDrawPath;
        this.time = time;
        this.isLook = isLook;
    }
    @Generated(hash = 269018259)
    public BookBean() {
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
    public int getBookId() {
        return this.bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public String getSubtypeStr() {
        return this.subtypeStr;
    }
    public void setSubtypeStr(String subtypeStr) {
        this.subtypeStr = subtypeStr;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getBookName() {
        return this.bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public String getBookDesc() {
        return this.bookDesc;
    }
    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getGrade() {
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getSupply() {
        return this.supply;
    }
    public void setSupply(String supply) {
        this.supply = supply;
    }
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getBookPath() {
        return this.bookPath;
    }
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    public String getBookDrawPath() {
        return this.bookDrawPath;
    }
    public void setBookDrawPath(String bookDrawPath) {
        this.bookDrawPath = bookDrawPath;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public boolean getIsLook() {
        return this.isLook;
    }
    public void setIsLook(boolean isLook) {
        this.isLook = isLook;
    }

}
