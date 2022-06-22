package com.bll.lnkstudy.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class NoteBook {

    @Id(autoincrement = true)
    public Long id;
    public String name;
    public int type;

    @Generated(hash = 2049909941)
    public NoteBook(Long id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    @Generated(hash = 2066935268)
    public NoteBook() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }

}
