package com.bll.lnkstudy.widget;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceGridItemDeco4 extends RecyclerView.ItemDecoration {

    private int width;
    private int height;

    public SpaceGridItemDeco4(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = width;
        outRect.bottom = height;
//        if (parent.getChildLayoutPosition(view) %4==0) {
//            outRect.left = 0;
//        }
    }

}