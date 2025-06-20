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
                helper.setImageResource(R.id.iv_tips,if (item.isExpanded) R.mipmap.icon_arrow_down else R.mipmap.icon_arrow_right)
                helper.setText(R.id.tv_title, item.title)
                helper.itemView.setOnClickListener {
                    val pos = helper.adapterPosition
                    if (item.hasSubItem()){
                        if (item.isExpanded) {
                            collapse(pos,false)
                        } else {
                            expand(pos,false)
                        }
                    }
                }
            }
            1-> {
                val childItem = multiItemEntity as CatalogChildBean
                val messageBean=childItem.messageBean
                helper.setText(R.id.tv_title, messageBean.title)
                helper.setText(R.id.tv_course, messageBean.subject)
                helper.setText(R.id.tv_type, messageBean.typeName)
                helper.setGone(R.id.tv_correct, messageBean.selfBatchStatus==1)
                helper.setGone(R.id.tv_standardTime,messageBean.minute>0)
                helper.setText(R.id.tv_standardTime,"${messageBean.minute}分钟")
                helper.setGone(R.id.iv_tips,!childItem.isLast)
                val timeStr=if (messageBean.endTime>0)"提交时间："+ DateUtils.longToStringWeek(messageBean.endTime) else "不提交"
                helper.setText(R.id.tv_end_date, timeStr)
                helper.itemView.setOnClickListener{
                    if (listener!=null)
                        listener?.onChildClick(childItem)
                }
            }
        }

    }

    private var listener: OnChildClickListener? = null

    fun interface OnChildClickListener{
        fun onChildClick(childBean: CatalogChildBean)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}