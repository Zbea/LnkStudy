package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DrawingCommitDialog(val context: Context, val screenPos:Int) {

    private var dialog:Dialog?=null
    private var mAdapter:MyAdapter?=null
    private var list= mutableListOf<Int>()

    fun builder(): DrawingCommitDialog? {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_drawing_commit)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog?.window
        val layoutParams =window?.attributes
        layoutParams?.width=DP2PX.dip2px(context,450f)
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,450f))/2
        }
        window?.attributes = layoutParams
        dialog?.show()

        val et_page=dialog?.findViewById<EditText>(R.id.et_page)
        val tv_save=dialog?.findViewById<TextView>(R.id.tv_save)
        tv_save?.setOnClickListener {
            val page=et_page?.text.toString()
            if (!page.isNullOrEmpty())
            {
                if (list.contains(page.toInt()))
                {
                    et_page?.setText("")
                }
                else{
                    list.add(page.toInt())
                    mAdapter?.setNewData(list)
                    et_page?.setText("")
                }

            }
        }

        val recyclerview = dialog?.findViewById<RecyclerView>(R.id.rv_list)
        recyclerview?.layoutManager = GridLayoutManager(context,6)
        mAdapter= MyAdapter(R.layout.item_drawing_commit_page, list)
        recyclerview?.adapter = mAdapter
        recyclerview?.addItemDecoration(SpaceGridItemDeco1(15,15))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->

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


    class MyAdapter(layoutResId: Int, data: List<Int>) : BaseQuickAdapter<Int, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, page: Int) {
            helper.setText(R.id.tv_name,page.toString())
        }

    }

}