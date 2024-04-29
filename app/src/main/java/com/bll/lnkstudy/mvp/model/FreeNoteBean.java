package com.bll.lnkstudy.mvp.model;

import com.bll.lnkstudy.utils.SPUtil;
import com.bll.lnkstudy.utils.greendao.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class FreeNoteBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId=Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;;
    public String title;
    public long date;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> paths;
    @Convert(columnType = String.class,converter = StringConverter.class)
    public List<String> bgRes;
    public int page;
    public boolean isSave;
    @Generated(hash = 1212939123)
    public FreeNoteBean(Long id, long userId, String title, long date, List<String> paths,
            List<String> bgRes, int page, boolean isSave) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.paths = paths;
        this.bgRes = bgRes;
        this.page = page;
        this.isSave = isSave;
    }
    @Generated(hash = 1976554700)
    public FreeNoteBean() {
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
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public List<String> getPaths() {
        return this.paths;
    }
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
    public List<String> getBgRes() {
        return this.bgRes;
    }
    public void setBgRes(List<String> bgRes) {
        this.bgRes = bgRes;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public boolean getIsSave() {
        return this.isSave;
    }
    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

}
