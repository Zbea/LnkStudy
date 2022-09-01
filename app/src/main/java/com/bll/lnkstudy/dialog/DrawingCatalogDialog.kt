package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DrawingCatalogDialog(val context: Context, val list: List<ListBean>) {

    private var dialog:Dialog?=null
    private var mAdapter:CatalogAdapter?=null

    fun builder(): DrawingCatalogDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_drawing_catalog)
        dialog?.show()

        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.BOTTOM or  Gravity.START
        layoutParams.y=DP2PX.dip2px(context,46f)
        window.attributes = layoutParams

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)

        recyclerview.layoutManager = LinearLayoutManager(context)
        mAdapter= CatalogAdapter(R.layout.item_catalog_parent, list)
        recyclerview.adapter = mAdapter
        mAdapter?.setOnItemClickListener  { adapter, view, position ->
            dismiss()
            if (listener!=null)
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

    fun setData(datas: List<ListBean>){
        mAdapter?.setNewData(datas)
    }

    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick(position: Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class CatalogAdapter(layoutResId: Int, data: List<ListBean>?) : BaseQuickAdapter<ListBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ListBean) {
            helper.setText(R.id.tv_name, item.name)
            helper.setText(R.id.tv_page, (item.page+1).toString())
        }

    }

}