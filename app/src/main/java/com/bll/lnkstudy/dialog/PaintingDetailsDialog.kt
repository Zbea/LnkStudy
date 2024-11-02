package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingDetailsBean
import com.bll.lnkstudy.ui.activity.PaintingImageActivity
import com.bll.lnkstudy.ui.adapter.PaintingDetailsAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.MaxRecyclerView
import com.bll.lnkstudy.widget.SpaceItemDeco

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

        val items= mutableListOf<PaintingDetailsBean>()
        for (item in DataBeanManager.popupPainting()){
            val list=PaintingBeanDaoManager.getInstance().queryPaintings(item.id)
            if (list.isNotEmpty()){
                items.add(PaintingDetailsBean().apply {
                    typeStr=item.name
                    num=list.size
                    this.list=list
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
            context.startActivity(Intent(context, PaintingImageActivity::class.java).setFlags(it.contentId))
        }

        return this
    }


}