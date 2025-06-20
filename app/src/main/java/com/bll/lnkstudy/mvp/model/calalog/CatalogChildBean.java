package com.bll.lnkstudy.mvp.model.calalog;

import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogChildBean implements Serializable, MultiItemEntity {

    public String title;
    public int parentPosition;
    public int pageNumber;
    public String picName;
    public HomeworkMessageList.MessageBean messageBean;
    public int position;
    public boolean isLast;

    @Override
    public int getItemType() {
        return 1;
    }
}
