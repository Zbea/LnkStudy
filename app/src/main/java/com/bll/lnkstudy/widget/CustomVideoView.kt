package com.bll.lnkstudy.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView: VideoView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(width, widthMeasureSpec)
        val height = getDefaultSize(height, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

}