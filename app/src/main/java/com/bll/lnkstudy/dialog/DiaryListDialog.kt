package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.mvp.model.DiaryBean
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class DiaryListDialog(val context: Context,val time:Long) {

    private var dialog:Dialog?=null

    fun builder(): DiaryListDialog? {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_diary_list)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.BOTTOM or  Gravity.START
        layoutParams.y=DP2PX.dip2px(context,38f)
        dialog?.show()

        val rv_list = dialog?.findViewById<RecyclerView>(R.id.rv_list)

        val list=DiaryDaoManager.getInstance().queryList()

        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter= CatalogAdapter(R.layout.item_diary_list, list ,time)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener  { adapter, view, position ->
            dismiss()
            listener?.onClick(list[position])
        }
        mAdapter.setOnItemChildClickListener  { adapter, view, position ->
            if (view.id==R.id.iv_delete){
                dismiss()
                listener?.onDelete(list[position])
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

    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick(diaryBean: DiaryBean)
        fun onDelete(diaryBean: DiaryBean)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class CatalogAdapter(layoutResId: Int, data: List<DiaryBean>,val time: Long) : BaseQuickAdapter<DiaryBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DiaryBean) {
            helper.setText(R.id.tv_name, item.title)
            helper.setVisible(R.id.iv_delete,time!=item.date)
            helper.addOnClickListener(R.id.iv_delete)
        }

    }

}