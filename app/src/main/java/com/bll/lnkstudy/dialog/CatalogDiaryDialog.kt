package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DiaryBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class CatalogDiaryDialog(val context: Context, private val oldScreen:Int, private val currentScreen:Int, val list: List<DiaryBean>) {

    private var dialog:Dialog?=null

    fun builder(): CatalogDiaryDialog {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_drawing_catalog)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = if (oldScreen==1)Gravity.BOTTOM or  Gravity.END  else Gravity.BOTTOM or  Gravity.START
        layoutParams.x=if (currentScreen==3) DP2PX.dip2px(context,1021f+42f)else DP2PX.dip2px(context,42f)
        layoutParams.y=DP2PX.dip2px(context,5f)
        dialog?.show()

        val rv_list = dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)

        val mAdapter= CatalogAdapter(R.layout.item_catalog_parent, list)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener  { adapter, view, position ->
            dismiss()
            listener?.onClick(position)
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

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(position: Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class CatalogAdapter(layoutResId: Int, data: List<DiaryBean>) : BaseQuickAdapter<DiaryBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DiaryBean) {
            helper.setText(R.id.tv_name, item.title)
            helper.setText(R.id.tv_page, DateUtils.longToStringDataNoYear(item.date))
            helper.setGone(R.id.iv_edit,false)
        }

    }

}