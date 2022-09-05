package com.bll.lnkstudy.ui.adapter

import android.widget.LinearLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.CatalogChildBean
import com.bll.lnkstudy.mvp.model.CatalogParentBean
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity

class BookCatalogAdapter(data: List<MultiItemEntity>?) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(0, R.layout.item_catalog_parent)
        addItemType(1, R.layout.item_catalog_child)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity?) {
        when (helper.itemViewType) {
            0 -> {
                val item= item as CatalogParentBean
                helper.setText(R.id.tv_name, item.title)
                helper.setText(R.id.tv_page, ""+item.pageNumber)
                helper.itemView.setOnClickListener { v ->
                    val pos = helper.adapterPosition
                    if (item.hasSubItem()){
                        if (item.isExpanded) {
                            collapse(pos)
                        } else {
                            expand(pos)
                        }
                    }
                    else{
                        if (listener!=null)
                            listener?.onParentClick(item.pageNumber)
                    }
                }
            }
            1-> {
                val childItem = item as CatalogChildBean
                helper.setText(R.id.tv_name, childItem.title)
                helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.black))
                helper.setText(R.id.tv_page,""+childItem.pageNumber)
                helper.getView<LinearLayout>(R.id.ll_click).setOnClickListener {
                    if (listener!=null)
                        listener?.onChildClick(item.pageNumber)
                }
            }
        }

    }

    private var listener: OnCatalogClickListener? = null

    interface OnCatalogClickListener{
        fun onParentClick(page:Int)
        fun onChildClick(page:Int)
    }

    fun setOnCatalogClickListener(listener: OnCatalogClickListener?) {
        this.listener = listener
    }

}