package com.bll.lnkstudy.mvp.model.homework;

import androidx.annotation.Nullable;

import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;

import java.util.Objects;

public class ParentTypeBean {
    public int id;
    public String name;
    public int subject;
    public long parentId;
    public int bookId;
    public int type;//1普通作业本2题卷本
    public String imageUrl;
    public long time;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof ParentTypeBean))
            return false;
        if (this==obj)
            return true;
        ParentTypeBean item=(ParentTypeBean) obj;
        return Objects.equals(this.id, item.id)&&this.parentId==item.parentId && Objects.equals(this.name, item.name) &&this.subject==item.subject
                &&this.type==item.type&&this.bookId==item.bookId;
    }
}
