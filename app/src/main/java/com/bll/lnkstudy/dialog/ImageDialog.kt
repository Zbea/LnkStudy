package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ToolUtils

class ImageDialog(val context: Context,private val screenPos:Int, private val images:List<String>,private val bgRes:List<String>){

    private var page=0
    private val total=images.size-1
    private var tvPage:TextView?=null
    private var ivImage:ImageView?=null
    private var ivBgImage:ImageView?=null

    constructor(context: Context,screenPos: Int,images:List<String>) : this(context,screenPos,images, mutableListOf())
    constructor(context: Context,images:List<String>) : this(context,2,images, mutableListOf())

    fun builder(): ImageDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_image)
        val window=dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,800F))/2
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,800F))/2
        }
        dialog.show()

        ivImage=dialog.findViewById(R.id.iv_image)
        ivBgImage=dialog.findViewById(R.id.iv_bgres)
        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        val rlPage=dialog.findViewById<RelativeLayout>(R.id.rl_page)
        val ivUp=dialog.findViewById<ImageView>(R.id.iv_up)
        val ivDown=dialog.findViewById<ImageView>(R.id.iv_down)
        tvPage=dialog.findViewById(R.id.tv_page)
        ivClose.setOnClickListener { dialog.dismiss() }

        if (images.isNotEmpty()){
            rlPage.visibility=View.VISIBLE
            setChange()
        }
        ivUp.setOnClickListener {
            if (page>0){
                page-=1
                setChange()
            }
        }

        ivDown.setOnClickListener {
            if (page<total){
                page+=1
                setChange()
            }
        }
        return this
    }

    private fun setChange(){
        if (bgRes.isNotEmpty())
            ivBgImage?.setBackgroundResource(ToolUtils.getImageResId(context,bgRes[page]))
        GlideUtils.setImageUrl(context,images[page],ivImage)
        tvPage?.text="${page+1}/${total+1}"
    }

}