package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateEvent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class DatePlanCopyDialog(private val context: Context,private val plans:MutableList<DateEvent>) {

    private var dialog:Dialog?=null
    private var position=0

    fun builder(): DatePlanCopyDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_date_plan_copy)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        plans[0].isCheck=true

        recyclerview.layoutManager = LinearLayoutManager(context)
        val mAdapter= MyAdapter(R.layout.item_date_plan_copy, plans)
        recyclerview.adapter = mAdapter
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            for (item in plans)
            {
                item.isCheck=false
            }
            plans[position].isCheck=true
            mAdapter?.notifyDataSetChanged()
        }

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        var okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

        cancleTv?.setOnClickListener { dismiss() }
        okTv?.setOnClickListener {
            dismiss()
            onSelectorListener?.onSelect(position)
        }
        return this
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var onSelectorListener: OnSelectorListener? = null

    fun interface OnSelectorListener {
        fun onSelect(position: Int)
    }

    fun setOnSelectorListener(onSelectorListener:OnSelectorListener) {
        this.onSelectorListener = onSelectorListener
    }

    class MyAdapter(layoutResId: Int, data: List<DateEvent>) : BaseQuickAdapter<DateEvent, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DateEvent) {
            helper.setText(R.id.tv_title,item.title)
            helper.setChecked(R.id.cb_check,item.isCheck)
        }

    }


}