package com.bll.lnkstudy.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceGridItemDeco5 extends RecyclerView.ItemDecoration {

    private int width;
    private int height;

    public SpaceGridItemDeco5(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = width;
        outRect.right=20;
        outRect.bottom = height;
    }

}