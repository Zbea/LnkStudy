package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ModuleBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class ModuleAddDialog(private val context: Context,private val screenPos:Int,val title:String,val list:MutableList<ModuleBean>) {

    private var dialog:Dialog?=null

    fun builder(): ModuleAddDialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_module_add)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,560f))/2
        }
        window.attributes = layoutParams


        val tvName = dialog?.findViewById<TextView>(R.id.tv_name)
        tvName?.text=title

        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel?.setOnClickListener { dialog?.dismiss() }

        var rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rvList?.layoutManager = GridLayoutManager(context,2)
        var mAdapter =MAdapter(R.layout.item_module, list)
        rvList?.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(0,20))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if (listener!=null)
                listener?.onClick(list[position])
            dismiss()
        }

        return this
    }


    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(item:ModuleBean)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    private class MAdapter(layoutResId: Int, data: List<ModuleBean>?) : BaseQuickAdapter<ModuleBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ModuleBean) {

            helper.setText(R.id.tv_name,item.name)

            helper.getView<ImageView>(R.id.iv_image).setImageResource(item.resId)

        }

    }

}