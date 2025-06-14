package com.bll.lnkstudy.mvp.model.calalog;

import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogChildBean implements Serializable, MultiItemEntity {

    public String title;
    public int parentPosition;
    public int pageNumber;
    public String picName;
    public long endTime;
    public String course;
    public String commonType;
    public int minute;
    public int selfBatchStatus;
    public HomeworkMessageList.MessageBean messageBean;

    @Override
    public int getItemType() {
        return 1;
    }
}
