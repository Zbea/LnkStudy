package com.bll.lnkstudy.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDetailsBean
import com.bll.lnkstudy.widget.FlowLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaintingDetailsAdapter(layoutResId: Int, data: List<PaintingDetailsBean>?) : BaseQuickAdapter<PaintingDetailsBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PaintingDetailsBean) {
        helper.setText(R.id.tv_book_type,item.typeStr)
        helper.setText(R.id.tv_book_num,"小计："+item.num+"幅")

        val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = FlowLayoutManager()
        val mAdapter = ChildAdapter(R.layout.item_bookcase_name,item.list)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(item.list[position])
        }
    }

    class ChildAdapter(layoutResId: Int,  data: List<PaintingBean>?) : BaseQuickAdapter<PaintingBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: PaintingBean) {
            helper.apply {
                helper.setText(R.id.tv_name, item.title)
            }
        }
    }


    private var listener: OnChildClickListener? = null

    fun interface OnChildClickListener {
        fun onClick(item: PaintingBean)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}
