package com.bll.lnkstudy.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.calalog.CatalogChild
import com.bll.lnkstudy.mvp.model.calalog.CatalogParent
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity

class PopupOperatingGuideCatalog(var context:Context,  var view: View) {

    private var mPopupWindow:PopupWindow?=null
    private var xoff=0

    fun builder(): PopupOperatingGuideCatalog{
        val popView = LayoutInflater.from(context).inflate(R.layout.popup_operating_guide_catalog, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView=popView
            isFocusable=true // 设置PopupWindow可获得焦点
            isTouchable=true // 设置PopupWindow可触摸
            isOutsideTouchable=true // 设置非PopupWindow区域可触摸
            isClippingEnabled = false
            width=DP2PX.dip2px(context,320f)
        }

        val rvList=popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        val mAdapter = CatalogAdapter(DataBeanManager.operatingGuideInfo())
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnCatalogClickListener { position,page->
            onSelectListener?.onClick(position,page)
            dismiss()
        }

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        xoff = mPopupWindow?.contentView?.measuredWidth!!

        show()
        return this
    }

    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view, 0, 5,Gravity.END)
        }
    }

   private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnSelectListener{
        fun onClick(position:Int,page:Int)
    }

    class CatalogAdapter(data: List<MultiItemEntity>?) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {
        init {
            addItemType(0, R.layout.item_catalog_parent)
            addItemType(1, R.layout.item_catalog_child)
        }
        override fun convert(helper: BaseViewHolder, multiItemEntity: MultiItemEntity?) {
            when (helper.itemViewType) {
                0 -> {
                    val item= multiItemEntity as CatalogParent
                    helper.setText(R.id.tv_name, item.title)
                    helper.itemView.setOnClickListener {
                        val pos = helper.adapterPosition
                        if (item.isExpanded) {
                            collapse(pos,false)
                        } else {
                            expand(pos,false)
                        }
                    }
                }
                1-> {
                    val childItem = multiItemEntity as CatalogChild
                    helper.setText(R.id.tv_name, "       "+childItem.title)
                    helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.black))
                    helper.setText(R.id.tv_page,"${childItem.pageNumber}")
                    helper.getView<LinearLayout>(R.id.ll_click).setOnClickListener {
                        if (listener!=null)
                            listener?.onClick(childItem.parentPosition,childItem.pageNumber)
                    }
                }
            }
        }
        private var listener: OnCatalogClickListener? = null
        fun interface OnCatalogClickListener{
            fun onClick(position:Int,page:Int)
        }
        fun setOnCatalogClickListener(listener: OnCatalogClickListener?) {
            this.listener = listener
        }
    }

}