package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class Notebook implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= SPUtil.INSTANCE.getObj("user",User.class).accountId;
    public String title;
    public int type;//笔记分类id
    public long createDate; //创建时间
    public String dateStr;
    public String contentResId; //笔记内容背景id
    public boolean isEncrypt;//是否加密
    public String encrypt;//密码

    @Generated(hash = 981317526)
    public Notebook(Long id, long userId, String title, int type, long createDate,
            String dateStr, String contentResId, boolean isEncrypt,
            String encrypt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.createDate = createDate;
        this.dateStr = dateStr;
        this.contentResId = contentResId;
        this.isEncrypt = isEncrypt;
        this.encrypt = encrypt;
    }
    @Generated(hash = 1348176405)
    public Notebook() {
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public String getDateStr() {
        return this.dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    public String getContentResId() {
        return this.contentResId;
    }
    public void setContentResId(String contentResId) {
        this.contentResId = contentResId;
    }
    public boolean getIsEncrypt() {
        return this.isEncrypt;
    }
    public void setIsEncrypt(boolean isEncrypt) {
        this.isEncrypt = isEncrypt;
    }
    public String getEncrypt() {
        return this.encrypt;
    }
    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

   

  


}
