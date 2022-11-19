package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;
import java.util.Objects;

public class DateRemind implements Serializable {

    public String remind;//提醒文字
    public int remindIn;//提醒时间
    public boolean isCheck;
}
