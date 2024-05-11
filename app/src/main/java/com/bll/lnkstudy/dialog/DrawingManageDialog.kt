package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DrawingManageDialog(val context:Context, var list:MutableList<PopupBean>) {

    fun builder(): DrawingManageDialog{
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_drawing_btn)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.START
        layoutParams?.x = DP2PX.dip2px(context, 42f)
        layoutParams?.y=DP2PX.dip2px(context, 516f)
        dialog.show()

        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        val mAdapter = MAdapter(R.layout.item_popwindow_btn, list)
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList.addItemDecoration(SpaceItemDeco(1,true))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (onSelectListener!=null)
                onSelectListener?.onClick(list[position])
            dialog.dismiss()
        }

        return this
    }

    private class MAdapter(layoutResId: Int, data: List<PopupBean>?) : BaseQuickAdapter<PopupBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: PopupBean) {
            helper.setText(R.id.tv_name,item.name)
        }
    }

   private var onSelectListener:OnClickListener?=null

    fun setOnSelectListener(onSelectListener:OnClickListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnClickListener{
        fun onClick(item: PopupBean)
    }

}