package com.bll.lnkstudy.base

import kotlinx.android.synthetic.main.ac_drawing_file.*


abstract class BaseFileDrawingActivity : BaseDrawingActivity() {

    override fun onInStanceElik() {
        if (v_content_a!=null && v_content_b!=null){
            elik_a = v_content_a?.pwInterFace
        }
        if (v_content_b!=null){
            elik_b = v_content_b?.pwInterFace
        }
    }

}


