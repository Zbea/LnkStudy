package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DataList
import com.bll.lnkstudy.ui.adapter.BookCatalogAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * type=0绘图目录 type=1书籍目录
 */
class DrawingCatalogDialog(val context: Context, val list: List<Any> ,val type:Int=0) {

    private var dialog:Dialog?=null

    fun builder(): DrawingCatalogDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_drawing_catalog)
        dialog?.show()

        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.BOTTOM or  Gravity.START
        layoutParams.y=DP2PX.dip2px(context,32f)
        window.attributes = layoutParams

        val rv_list = dialog?.findViewById<RecyclerView>(R.id.rv_list)

        rv_list?.layoutManager = LinearLayoutManager(context)

        if (type==0){
            var mAdapter= CatalogAdapter(R.layout.item_catalog_parent, list as List<DataList>)
            rv_list?.adapter = mAdapter
            mAdapter?.bindToRecyclerView(rv_list)
            mAdapter?.setOnItemClickListener  { adapter, view, position ->
                dismiss()
                if (listener!=null)
                    listener?.onClick(position)
            }
        }
        else{
            val layoutManager=LinearLayoutManager(context)
            //设置布局管理器
            rv_list?.layoutManager =layoutManager
            var mAdapter = BookCatalogAdapter(list as List<MultiItemEntity>)
            rv_list?.adapter = mAdapter
            mAdapter?.bindToRecyclerView(rv_list)
            mAdapter?.setOnCatalogClickListener(object : BookCatalogAdapter.OnCatalogClickListener {
                override fun onParentClick(page: Int) {
                    dismiss()
                    if (listener!=null)
                        listener?.onClick(page)
                }
                override fun onChildClick(page: Int) {
                    dismiss()
                    if (listener!=null)
                        listener?.onClick(page)
                }
            })
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

    class CatalogAdapter(layoutResId: Int, data: List<DataList>) : BaseQuickAdapter<DataList, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DataList) {
            helper.setText(R.id.tv_name, item.name)
            helper.setText(R.id.tv_page, (item.page+1).toString())
        }

    }

}