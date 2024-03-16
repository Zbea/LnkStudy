package com.bll.lnkstudy.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDeco(private val topOffset: Int,private val rightOffset: Int,private val leftOffset: Int,private val bottomOffset: Int,private val isLiner :Boolean)
    : RecyclerView.ItemDecoration() {

    private val mColorPaint = Paint()

    constructor(bottomOffset: Int,isLiner: Boolean):this(0,0,0,bottomOffset,isLiner)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        mColorPaint.color=Color.parseColor("#ff707070")
        if (isLiner){
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val  top = child.bottom + params.bottomMargin
                val  bottom = top + bottomOffset
                if (i!=childCount-1){
                    c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(),
                        bottom.toFloat(), mColorPaint)
                }
            }
        }
    }

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