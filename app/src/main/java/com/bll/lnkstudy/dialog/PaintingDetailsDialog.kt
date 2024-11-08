package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.ItemDetailsBean
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.FlowLayoutManager
import com.bll.lnkstudy.widget.MaxRecyclerView
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PaintingDetailsDialog(val context: Context) {

    fun builder(): PaintingDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_bookcase_list)
        val window= dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,600F))/2
        dialog.show()

        val total=PaintingBeanDaoManager.getInstance().queryPaintings().size

        val tv_title=dialog.findViewById<TextView>(R.id.tv_title)
        tv_title.text="书画明细"

        val tv_total=dialog.findViewById<TextView>(R.id.tv_book_total)
        tv_total.text="总计：${total}幅"

        val items= mutableListOf<ItemDetailsBean>()
        for (item in DataBeanManager.popupPainting()){
            val list=PaintingBeanDaoManager.getInstance().queryPaintings(item.id)
            if (list.isNotEmpty()){
                items.add(ItemDetailsBean().apply {
                    typeStr=item.name
                    num=list.size
                    this.paintings=list
                })
            }
        }

        val rv_list=dialog.findViewById<MaxRecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = PaintingDetailsAdapter(R.layout.item_bookcase_list, items)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(30))
        mAdapter.setOnChildClickListener{
            dialog.dismiss()
            MethodManager.gotoPaintingImage(context,it.contentId,2)
        }

        return this
    }


    class PaintingDetailsAdapter(layoutResId: Int, data: List<ItemDetailsBean>?) : BaseQuickAdapter<ItemDetailsBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ItemDetailsBean) {
            helper.setText(R.id.tv_book_type,item.typeStr)
            helper.setText(R.id.tv_book_num,"小计："+item.num+"幅")

            val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
            recyclerView?.layoutManager = FlowLayoutManager()
            val mAdapter = ChildAdapter(R.layout.item_bookcase_name,item.paintings)
            recyclerView?.adapter = mAdapter
            mAdapter.setOnItemClickListener { adapter, view, position ->
                listener?.onClick(item.paintings[position])
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

}