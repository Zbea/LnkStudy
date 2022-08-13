package com.bll.lnkstudy.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bll.lnkstudy.R

class RoundImageView: AppCompatImageView {

    private var rightBottomRadius: Int
    private var leftBottomRadius: Int
    private var rightTopRadius: Int
    private var leftTopRadius: Int
    private var radius: Int =0

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            :super(context, attributeSet, defStyleAttr) {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.RoundImageView)
        val defaultRadius = 0
        radius = typeArray.getDimensionPixelOffset(R.styleable.RoundImageView_radius, defaultRadius)
        leftTopRadius = typeArray.getDimensionPixelOffset(
            R.styleable.RoundImageView_leftTopRadius,
            defaultRadius
        )
        rightTopRadius = typeArray.getDimensionPixelOffset(
            R.styleable.RoundImageView_rightTopRadius,
            defaultRadius
        )
        leftBottomRadius = typeArray.getDimensionPixelOffset(
            R.styleable.RoundImageView_leftBottomRadius,
            defaultRadius
        )
        rightBottomRadius = typeArray.getDimensionPixelOffset(
            R.styleable.RoundImageView_rightBottomRadius,
            defaultRadius
        )
        if (radius != 0) {
            if (leftTopRadius == 0) {
                leftTopRadius = radius
            }
            if (rightTopRadius == 0) {
                rightTopRadius = radius;
            }
            if (leftBottomRadius == 0) {
                leftBottomRadius = radius;
            }
            if (rightBottomRadius == 0) {
                rightBottomRadius = radius;
            }
        }
        typeArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        // 保证图片宽高大于圆角宽高， 获取圆角的宽高
        // 取横着大的长度
        val maxLeft = Math.max(leftTopRadius, leftBottomRadius)
        val maxRight = Math.max(rightTopRadius, rightBottomRadius)
        val minWidth = maxLeft + maxRight
        // 取竖着大的长度
        val maxTop = Math.max(leftTopRadius, rightTopRadius)
        val maxBottom = Math.max(leftBottomRadius, rightBottomRadius)
        val minHeight = maxTop + maxBottom
        if (width > minWidth && height > minHeight) {
            val path = Path()
            //四个角：右上，右下，左下，左上
            path.moveTo(leftTopRadius.toFloat(), 0F)

            path.lineTo((width - rightTopRadius).toFloat() , 0F)
            path.quadTo(width.toFloat(), 0F, width.toFloat(), rightTopRadius.toFloat())

            path.lineTo(width.toFloat(), (height - rightBottomRadius).toFloat())
            path.quadTo(width.toFloat(), height.toFloat(), (width - rightBottomRadius).toFloat(), height.toFloat())

            path.lineTo(leftBottomRadius.toFloat(), height.toFloat())
            path.quadTo(0F, height.toFloat(), 0F, (height - leftBottomRadius).toFloat())

            path.lineTo(0F, leftTopRadius.toFloat())
            path.quadTo(0F, 0F, leftTopRadius.toFloat(), 0F)

            canvas!!.clipPath(path)

            /*val paint = Paint()
            paint.setColor(Color.BLUE)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas!!.drawPath(path, paint)*/
        }
        super.onDraw(canvas)
    }

}