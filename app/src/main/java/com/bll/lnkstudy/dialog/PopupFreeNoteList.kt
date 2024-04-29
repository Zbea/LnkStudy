package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
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

class PopupFreeNoteList(var context: Context, var view: View,private var date:Long) {

    private var list= mutableListOf<FreeNoteBean>()
    private var mPopupWindow: PopupWindow? = null
    private var mAdapter: MAdapter?=null
    private var pageIndex=1
    private var pageSize=11
    private var pageCount=0

    fun builder(): PopupFreeNoteList {
        val popView = LayoutInflater.from(context).inflate(R.layout.popup_freenote_list, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView = popView
            isFocusable = true // 设置PopupWindow可获得焦点
            isTouchable = true // 设置PopupWindow可触摸
            isOutsideTouchable = true // 设置非PopupWindow区域可触摸
            width=DP2PX.dip2px(context,280f)
        }

        val total=FreeNoteDaoManager.getInstance().queryList().size

        val ll_page_number = popView.findViewById<LinearLayout>(R.id.ll_page_number)
        val tv_page_current = popView.findViewById<TextView>(R.id.tv_page_current)
        val tv_page_total = popView.findViewById<TextView>(R.id.tv_page_total)

        pageCount = ceil(total.toDouble() / pageSize).toInt()
        if (total == 0) {
            ll_page_number?.visibility=View.INVISIBLE
        } else {
            tv_page_current?.text = pageIndex.toString()
            tv_page_total?.text = pageCount.toString()
            ll_page_number?.visibility=View.VISIBLE
        }

        val btn_page_up = popView.findViewById<TextView>(R.id.btn_page_up)
        val btn_page_down = popView.findViewById<TextView>(R.id.btn_page_down)

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

        val rvList = popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        mAdapter = MAdapter(R.layout.item_freenote, null,date)
        rvList.adapter=mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            onSelectListener?.onSelect(list[position])
            dismiss()
        }

        findFreeNotes()

        show()
        return this
    }

    private fun findFreeNotes(){
        list=FreeNoteDaoManager.getInstance().queryList(pageIndex,pageSize)
        mAdapter?.setNewData(list)
    }


    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view, 0, 0, Gravity.RIGHT)
        }
    }

    private var onSelectListener: OnSelectListener?=null

    fun setOnSelectListener(onSelectListener: OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnSelectListener{
        fun onSelect(item: FreeNoteBean)
    }


    private class MAdapter(layoutResId: Int, data: List<FreeNoteBean>?,private var date:Long) :
        BaseQuickAdapter<FreeNoteBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: FreeNoteBean) {
            helper.setText(R.id.tv_title, item.title)
            helper.setVisible(R.id.iv_now,date==item.date)
        }

    }

}