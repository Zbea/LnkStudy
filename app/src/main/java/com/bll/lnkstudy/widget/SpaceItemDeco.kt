package com.bll.lnkstudy.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDeco(private val topOffset: Int,private val rightOffset: Int,private val leftOffset: Int,private val bottomOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val count=state.itemCount
        val position=parent.getChildAdapterPosition(view)
        if (position==count-1){
            outRect.set(leftOffset,topOffset,rightOffset,0)
        }
        else{
            outRect.set(leftOffset,topOffset,rightOffset,bottomOffset)
        }
    }


}