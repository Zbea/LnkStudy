package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemDetailsBean
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.FlowLayoutManager
import com.bll.lnkstudy.widget.MaxRecyclerView
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class ScreenshotDetailsDialog(val context: Context) {

    fun builder(): ScreenshotDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_bookcase_list)
        val window= dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        var total=0
        val items= mutableListOf<ItemDetailsBean>()

        val screenTypes= ItemTypeDaoManager.getInstance().queryAll(1)
        screenTypes.add(0, ItemTypeBean().apply {
            path= FileAddress().getPathScreen("未分类")
            title="未分类"
        })

        for (item in screenTypes){
            val files= FileUtils.getDescFiles(item.path)
            if (files.isNotEmpty()){
                items.add(ItemDetailsBean().apply {
                    typeStr=item.title
                    num=files.size
                    this.screens=files
                })
                total+=files.size
            }
        }

        val tv_title=dialog.findViewById<TextView>(R.id.tv_title)
        tv_title.text="截图明细"

        val tv_total=dialog.findViewById<TextView>(R.id.tv_book_total)
        tv_total.text="总计：${total}"

        val rv_list=dialog.findViewById<MaxRecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = ScreenshotDetailsAdapter(R.layout.item_details_list, items)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(30))
        mAdapter.setOnChildClickListener{ parentPos,pos->
//            dialog.dismiss()
//            val path=screenTypes[parentPos].path
//            val files=FileUtils.getDescFiles(path)
//            MethodManager.gotoScreenFile(context,files.size-pos-1,screenTypes[parentPos].path)
        }

        return this
    }


    class ScreenshotDetailsAdapter(layoutResId: Int, data: List<ItemDetailsBean>?) : BaseQuickAdapter<ItemDetailsBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ItemDetailsBean) {
            helper.setText(R.id.tv_book_type,item.typeStr)
            helper.setText(R.id.tv_book_num,"( ${item.num} )")

            val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
            recyclerView?.layoutManager = FlowLayoutManager()
            val mAdapter = ChildAdapter(R.layout.item_details_list_name,item.screens)
            recyclerView?.adapter = mAdapter
            mAdapter.setOnItemClickListener { adapter, view, position ->
                listener?.onClick(helper.adapterPosition,position)
            }
        }

        class ChildAdapter(layoutResId: Int,  data: List<File>?) : BaseQuickAdapter<File, BaseViewHolder>(layoutResId, data) {
            override fun convert(helper: BaseViewHolder, item: File) {
                helper.apply {
                    helper.setText(R.id.tv_name, item.name.replace(".png",""))
                }
            }
        }

        private var listener: OnChildClickListener? = null

        fun interface OnChildClickListener {
            fun onClick(parentPos:Int,pos: Int)
        }
        fun setOnChildClickListener(listener: OnChildClickListener?) {
            this.listener = listener
        }
    }

}