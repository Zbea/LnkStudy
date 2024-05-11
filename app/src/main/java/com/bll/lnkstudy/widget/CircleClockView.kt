package com.bll.lnkstudy.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class CircleClockView : ImageView {
    private var paint: Paint? = null
    private var rectF: RectF? = null
    private var radius = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.color = Color.TRANSPARENT
        rectF = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(widthSize, heightSize)
        setMeasuredDimension(size, size)
        radius = size / 2
        rectF!![0f, 0f, radius.toFloat()] = radius.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF!!, paint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x - radius
            val y = event.y - radius
            val distance = Math.sqrt((x * x + y * y).toDouble()).toFloat()
            var angle = Math.atan2(y.toDouble(), x.toDouble()) // 转换为弧度
            // 转换为标准的0-2π范围
            while (angle < 0) {
                angle += 2 * Math.PI
            }
            if (distance < radius * 0.75) {
                val hour = (angle / (Math.PI / 6) + 3.5).toInt() % 12 // 转换为时钟的小时数
//                Log.d("debug", "hour:$hour")
                listener!!.onClick(0, hour)
            } else {
                val minute = ((angle / (Math.PI / 30) + 15.5) % 60).toInt() // 转换为时钟的分钟数
//                Log.d("debug", "minute:$minute")
                listener!!.onClick(1, minute)
            }
            return true
        }
        return false
    }

    var listener: OnClockClickListener? = null

    fun interface OnClockClickListener {
        fun onClick(type: Int, time: Int)
    }

    fun setOnClockClickListener(onClickListener: OnClockClickListener?) {
        listener = onClickListener
    }
}