package com.bll.lnkstudy.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceGridItemDeco1 extends RecyclerView.ItemDecoration {

    private int width;
    private int height;

    public SpaceGridItemDeco1(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = width;
        outRect.right=width;
        outRect.bottom = height;
    }

}