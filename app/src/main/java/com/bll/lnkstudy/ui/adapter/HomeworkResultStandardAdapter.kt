package com.bll.lnkstudy.ui.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.ResultStandardItem
import com.bll.lnkstudy.mvp.model.homework.ResultStandardItem.ResultChildItem
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkResultStandardAdapter(layoutResId: Int,val correctModule:Int, data: List<ResultStandardItem>?) : BaseQuickAdapter<ResultStandardItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ResultStandardItem) {
        helper.setText(R.id.tv_title,if(data.size==1)"" else item.title)
        helper.setGone(R.id.tv_title,data.size!=1)

        val layoutId=if (correctModule==2) R.layout.item_homework_result_standard_child_high else R.layout.item_homework_result_standard_child
        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        if (correctModule==2&&data.size==1){
            val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            recyclerView.layoutParams=layoutParams
        }
        if (recyclerView.adapter==null){
            recyclerView?.layoutManager = GridLayoutManager(mContext,3)
            val mAdapter = ChildAdapter(layoutId,correctModule,data.size==4,item.list)
            recyclerView?.adapter = mAdapter
            if (correctModule==2&&recyclerView.itemDecorationCount==0)
                recyclerView.addItemDecoration(SpaceGridItemDeco1(3,5,if (data.size==4)30 else 50))
            mAdapter.bindToRecyclerView(recyclerView)
            mAdapter.setOnItemClickListener { adapter, view, position ->
                listener?.onClick(helper.adapterPosition,position)
            }
        }
        else{
            (recyclerView.adapter as ChildAdapter).setNewData(item.list)
        }
    }

    class ChildAdapter(layoutResId: Int, val correctModule:Int, private val isVertical:Boolean, data: List<ResultChildItem>?) : BaseQuickAdapter<ResultChildItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ResultChildItem) {
            helper.setText(R.id.tv_score,item.sortStr)
            if (isVertical&&correctModule==2) {
                helper.setImageResource(R.id.iv_result,if (item.isCheck) R.mipmap.icon_correct_right_vertical else R.mipmap.icon_correct_wrong_vertical)
            }
            else{
                helper.setImageResource(R.id.iv_result,if (item.isCheck) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
            }
        }
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position:Int,childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }

}

