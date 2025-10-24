package com.bll.lnkstudy.ui.activity.drawing

import android.view.Gravity
import android.view.PWDrawObjectHandler
import android.widget.RadioButton
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import kotlinx.android.synthetic.main.ac_drawing_draft.iv_change
import kotlinx.android.synthetic.main.ac_drawing_draft.iv_clear
import kotlinx.android.synthetic.main.ac_drawing_draft.iv_top
import kotlinx.android.synthetic.main.ac_drawing_draft.rg_group

/**
 * 草稿纸
 */
class DraftDrawingActivity:BaseDrawingActivity(){

    private val paths= mutableListOf<String>()
    private val pos= 0
    private var isTop=false

    override fun layoutId(): Int {
        return R.layout.ac_drawing_draft
    }

    override fun initData() {
        for (i in 0..4){
            paths.add(FileAddress().getPathDraft()+"/$i.png")
        }
    }

    override fun initView() {
        val layoutParams=window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        window?.attributes = layoutParams

        onClick(pos)
        (rg_group.getChildAt(pos) as RadioButton).isChecked=true

        iv_clear.setOnClickListener {
            elik_b ?.clearContent(null,true,true)
            if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
        }

        iv_top.setOnClickListener {
            isTop=!isTop
            if (isTop){
                layoutParams?.y= DP2PX.dip2px(this,38f)
                layoutParams?.gravity = Gravity.TOP
                window?.attributes = layoutParams
            }else{
                layoutParams?.gravity = Gravity.BOTTOM
                window?.attributes = layoutParams
            }
        }

        iv_change.setOnClickListener {
            when(getCurrentScreenPos()){
                0->{
                    moveToScreen(2)
                }
                1->{
                    moveToScreen(2)
                }
                2->{
                    moveToScreen(1)
                }
            }
        }

        rg_group.setOnCheckedChangeListener { p0, id ->
            when(id){
                R.id.rb_1->{
                    onClick(0)
                }
                R.id.rb_2->{
                    onClick(1)
                }
                R.id.rb_3->{
                    onClick(2)
                }
                R.id.rb_4->{
                    onClick(3)
                }
                R.id.rb_5->{
                    onClick(4)
                }
            }
        }

    }

    private fun onClick(index:Int){
        elik_b?.setLoadFilePath(paths[index], true)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (str in paths){
            FileUtils.delete(str)
        }
    }

}