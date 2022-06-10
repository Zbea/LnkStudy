package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;
import java.util.Objects;

public class DateRemind implements Serializable {

    public String remind;//提醒文字
    public int remindIn;//提醒时间

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRemind that = (DateRemind) o;
        return remindIn == that.remindIn && Objects.equals(remind, that.remind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remind, remindIn);
    }
}
