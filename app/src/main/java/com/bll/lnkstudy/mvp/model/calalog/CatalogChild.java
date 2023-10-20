package com.bll.lnkstudy.mvp.model.calalog;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogChild implements Serializable, MultiItemEntity {

    public String title;
    public int pageNumber;
    public String picName;

    @Override
    public int getItemType() {
        return 1;
    }
}
