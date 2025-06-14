package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity

class HomeworkMessageAllAdapter(data: MutableList<MultiItemEntity>?) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(0, R.layout.item_homework_message_all_parent)
        addItemType(1, R.layout.item_homework_message_all_list)
    }

    override fun convert(helper: BaseViewHolder, multiItemEntity: MultiItemEntity?) {
        when (helper.itemViewType) {
            0 -> {
                val item= multiItemEntity as CatalogParentBean
                helper.setText(R.id.tv_title, item.title)
            }
            1-> {
                val childItem = multiItemEntity as CatalogChildBean
                helper.setText(R.id.tv_title, childItem.title)
                helper.setText(R.id.tv_course, childItem.course)
                helper.setText(R.id.tv_type, childItem.commonType)
                helper.setGone(R.id.tv_correct, childItem.selfBatchStatus==1)
                helper.setGone(R.id.tv_standardTime,childItem.minute>0)
                helper.setText(R.id.tv_standardTime,"${childItem.minute}分钟")
                val timeStr=if (childItem.endTime>0)"提交时间："+ DateUtils.longToStringWeek(childItem.endTime) else "不提交"
                helper.setText(R.id.tv_end_date, timeStr)
//                helper.getView<LinearLayout>(R.id.ll_click).setOnClickListener {
//                    if (listener!=null)
//                        listener?.onChildClick(multiItemEntity.pageNumber)
//                }
            }
        }

    }

    private var listener: OnChildClickListener? = null

    interface OnChildClickListener{
        fun onChildClick(page:Int)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}