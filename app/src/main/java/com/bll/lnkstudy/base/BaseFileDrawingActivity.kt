package com.bll.lnkstudy.base

import android.widget.LinearLayout
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.ac_drawing_file.*


abstract class BaseFileDrawingActivity : BaseDrawingActivity() {

    private var ll_content_a:LinearLayout?=null
    private var ll_content_b:LinearLayout?=null

    override fun onInStanceElik() {
        ll_content_a=findViewById(R.id.ll_content_a)
        ll_content_b=findViewById(R.id.ll_content_b)
        elik_a = ll_content_a?.pwInterFace
        elik_b = ll_content_b?.pwInterFace
    }


    override fun onChangeExpandView() {
        if (screenPos== Constants.SCREEN_LEFT){
            if (!isExpand){
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(ll_content_a)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(ll_content_b)
                ll_draw_content?.addView(ll_page_content_b)
                disMissView(ll_page_content_a,ll_content_a)
                showView(ll_page_content_b,ll_content_b)
            }
            else{
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(ll_content_a)
                ll_draw_content?.addView(ll_page_content_b)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(ll_content_b)
                showView(ll_page_content_a,ll_content_a,ll_page_content_b,ll_content_b)
            }
        }
        else if (screenPos== Constants.SCREEN_RIGHT){
            if (!isExpand){
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(ll_page_content_b)
                ll_draw_content?.addView(ll_content_a)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(ll_content_b)
                disMissView(ll_page_content_a,ll_content_a)
                showView(ll_page_content_b,ll_content_b)
            }
            else{
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(ll_content_a)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(ll_page_content_b)
                ll_draw_content?.addView(ll_content_b)
                showView(ll_page_content_a,ll_content_a,ll_page_content_b,ll_content_b)
            }
        }
    }

}


