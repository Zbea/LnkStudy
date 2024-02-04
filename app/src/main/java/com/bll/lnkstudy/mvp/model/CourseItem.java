package com.bll.lnkstudy.mvp.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class CourseItem implements Serializable {

    public String subject;
    public String teacher;
    public long userId;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof CourseItem))
            return false;
        if (this==obj)
            return true;
        CourseItem item=(CourseItem) obj;
        return this.userId==item.userId&& Objects.equals(this.subject, item.subject) && Objects.equals(this.teacher, item.teacher);
    }

}
