package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.ui.adapter.BookCatalogAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity


class CatalogDialog(val context: Context, private val oldScreen:Int, private val currentScreen:Int, val list: List<Any>, val type:Int=0, private val startCount:Int) {

    private var dialog:Dialog?=null
    constructor(context: Context,screenPos:Int,currentScreen:Int, list: List<Any>):this(context, screenPos,currentScreen,list, 0, 0)

    fun builder(): CatalogDialog {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_drawing_catalog)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = if (oldScreen==1)Gravity.BOTTOM or  Gravity.END  else Gravity.BOTTOM or  Gravity.START
        layoutParams.x=if (currentScreen==3) DP2PX.dip2px(context,1021f+42f)else DP2PX.dip2px(context,42f)
        layoutParams.y= DP2PX.dip2px(context,5f)
        dialog?.show()

        val rv_list = dialog?.findViewById<RecyclerView>(R.id.rv_list)

        rv_list?.layoutManager = LinearLayoutManager(context)

        if (type==0){
            val mAdapter= CatalogAdapter(R.layout.item_catalog_parent, list as List<ItemList>)
            rv_list?.adapter = mAdapter
            mAdapter.bindToRecyclerView(rv_list)
            mAdapter.setOnItemClickListener  { adapter, view, position ->
                dismiss()
                if (listener!=null)
                    listener?.onClick(position)
            }
            mAdapter.setOnItemChildClickListener { adapter, view, position ->
                val item=list[position]
                InputContentDialog(context, oldScreen ,item.name).builder().setOnDialogClickListener{
                    item.name=it
                    mAdapter.notifyItemChanged(position)
                    listener?.onEdit(position,it)
                }
            }
        }
        else{
            val layoutManager=LinearLayoutManager(context)
            //设置布局管理器
            rv_list?.layoutManager =layoutManager
            val mAdapter = BookCatalogAdapter(list as List<MultiItemEntity>,startCount)
            rv_list?.adapter = mAdapter
            mAdapter.bindToRecyclerView(rv_list)
            mAdapter.setOnCatalogClickListener(object : BookCatalogAdapter.OnCatalogClickListener {
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

    interface OnDialogClickListener {
        fun onClick(position: Int)
        fun onEdit(position: Int,title:String){}
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class CatalogAdapter(layoutResId: Int, data: List<ItemList>) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setText(R.id.tv_name, item.name)
            helper.setText(R.id.tv_page, (item.page+1).toString())
            helper.setGone(R.id.iv_edit,item.isEdit)
            helper.addOnClickListener(R.id.iv_edit)
        }

    }


}