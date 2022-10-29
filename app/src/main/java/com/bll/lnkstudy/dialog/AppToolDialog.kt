package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco

class AppToolDialog(val context: Context, val screenPos:Int, private val lists:  List<AppBean>) {

    private var dialog:Dialog?=null

    fun builder(): AppToolDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_app_tool)
        dialog?.show()
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window?.attributes
        if (screenPos==1){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,650f))/2
        }
        if (screenPos==2){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,650f))/2
        }
        window?.attributes = layoutParams

        val ivCancel=dialog?.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel?.setOnClickListener {
            dismiss()
        }
        val rv_list=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,4)//创建布局管理
        val mAdapter = AppListAdapter(1,R.layout.item_app_list, lists)
        rv_list?.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco(0,50))
        mAdapter?.setOnItemChildClickListener{ adapter, view, position ->
            if (view.id==R.id.iv_image){
                val packageName= lists[position].packageName
                AppUtils.startAPP(context,packageName)
            }
        }

        return this
    }

    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }



}