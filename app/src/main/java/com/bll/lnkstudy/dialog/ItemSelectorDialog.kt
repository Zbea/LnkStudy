package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ItemSelectorDialog(val context: Context, val titleStr: String,val items:MutableList<ItemList>) {

    fun builder(): ItemSelectorDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_item_select)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val tv_name = dialog.findViewById<TextView>(R.id.tv_name)
        tv_name.text=titleStr
        val iv_close = dialog.findViewById<ImageView>(R.id.iv_close)
        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,2)
        val mAdapter = MyAdapter(R.layout.item_select_name, items)
        rv_list?.adapter = mAdapter
        rv_list?.addItemDecoration(SpaceGridItemDeco2(20, DP2PX.dip2px(context,15f)))
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(position)
            dialog.dismiss()
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(pos: Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setText(R.id.tv_name,item.name)
        }
    }

}