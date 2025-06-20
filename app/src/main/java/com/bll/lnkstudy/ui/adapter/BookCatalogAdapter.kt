package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity

class BookCatalogAdapter(data: List<MultiItemEntity>?,private val startCount:Int) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(0, R.layout.item_catalog_parent)
        addItemType(1, R.layout.item_catalog_child)
    }

    override fun convert(helper: BaseViewHolder, multiItemEntity: MultiItemEntity?) {
        when (helper.itemViewType) {
            0 -> {
                val item= multiItemEntity as CatalogParentBean
                helper.setGone(R.id.iv_edit,false)
                helper.setText(R.id.tv_name, item.title)
                helper.setText(R.id.tv_page, "${item.pageNumber-startCount}")
                helper.itemView.setOnClickListener {
                    val pos = helper.adapterPosition
                    if (item.hasSubItem()){
                        if (item.isExpanded) {
                            collapse(pos,false)
                        } else {
                            expand(pos,false)
                        }
                    }
                    else{
                        if (listener!=null)
                            listener?.onParentClick(item.pageNumber)
                    }
                }
            }
            1-> {
                val childItem = multiItemEntity as CatalogChildBean
                helper.setText(R.id.tv_name, childItem.title)
                helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.black))
                helper.setText(R.id.tv_page,"${childItem.pageNumber-startCount}")
                helper.itemView.setOnClickListener {
                    if (listener!=null)
                        listener?.onChildClick(multiItemEntity.pageNumber)
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