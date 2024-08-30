package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.FreeNoteDaoManager
import com.bll.lnkstudy.mvp.model.FreeNoteBean
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlin.math.ceil

class CatalogFreeNoteDialog(var context: Context, private var date:Long) {

    private var list= mutableListOf<FreeNoteBean>()
    private var mAdapter: MAdapter?=null
    private var pageIndex=1
    private var pageSize=13
    private var pageCount=0

    fun builder(): CatalogFreeNoteDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_freenote_list)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.BOTTOM or  Gravity.START
        layoutParams.y=DP2PX.dip2px(context,5f)
        layoutParams.x=DP2PX.dip2px(context,42f)
        dialog.show()


        val total=FreeNoteDaoManager.getInstance().queryList().size

        val ll_page_number = dialog.findViewById<LinearLayout>(R.id.ll_page_number)
        val tv_page_current = dialog.findViewById<TextView>(R.id.tv_page_current)
        val tv_page_total = dialog.findViewById<TextView>(R.id.tv_page_total)

        pageCount = ceil(total.toDouble() / pageSize).toInt()
        if (total == 0) {
            ll_page_number?.visibility=View.INVISIBLE
        } else {
            tv_page_current?.text = pageIndex.toString()
            tv_page_total?.text = pageCount.toString()
            ll_page_number?.visibility=View.VISIBLE
        }

        val btn_page_up = dialog.findViewById<TextView>(R.id.btn_page_up)
        val btn_page_down = dialog.findViewById<TextView>(R.id.btn_page_down)

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                findFreeNotes()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                findFreeNotes()
            }
        }

        val rvList = dialog.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        mAdapter = MAdapter(R.layout.item_freenote, list,date)
        rvList.adapter=mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            onSelectListener?.onClick(list[position])
            dialog.dismiss()
        }
        findFreeNotes()

        return this
    }

    private fun findFreeNotes(){
        list=FreeNoteDaoManager.getInstance().queryList(pageIndex,pageSize)
        mAdapter?.setNewData(list)
    }

    private var onSelectListener: OnItemClickListener?=null

    fun setOnItemClickListener(onSelectListener: OnItemClickListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnItemClickListener{
        fun onClick(item: FreeNoteBean)
    }


    private class MAdapter(layoutResId: Int, data: List<FreeNoteBean>?,private val date: Long) :
        BaseQuickAdapter<FreeNoteBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: FreeNoteBean) {
            helper.setText(R.id.tv_title, item.title)
            helper.setVisible(R.id.iv_now,date==item.date)
        }

    }

}