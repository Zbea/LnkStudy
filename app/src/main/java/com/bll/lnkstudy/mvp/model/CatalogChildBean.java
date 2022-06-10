package com.bll.lnkstudy.mvp.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogChildBean implements Serializable, MultiItemEntity {

    public String title;
    public int pageNumber;
    public String picName;

    @Override
    public int getItemType() {
        return 1;
    }
}
