package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppToolDialog(val context: Context, val screenPos:Int, private val lists:  List<AppBean>) {

    private var dialog:Dialog?=null

    fun builder(): AppToolDialog? {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_app_tool)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.LEFT
        layoutParams?.x=DP2PX.dip2px(context,12f)
        layoutParams?.y=50
        if (screenPos==2){
            layoutParams?.gravity = Gravity.BOTTOM or Gravity.RIGHT
        }
        dialog?.show()

        val rv_list=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = MyAdapter(R.layout.item_app_name_list, lists)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (position==0){
                dismiss()
                listener?.onClick(position)
            }
            else{
                val packageName= lists[position].packageName
                AppUtils.startAPP(context,packageName)
            }
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

    class MyAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: AppBean) {
            helper.setText(R.id.tv_name,item.appName)
            helper.setImageDrawable(R.id.iv_image,BitmapUtils.byteToDrawable(item.imageByte))
        }
    }

    var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(position: Int)
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener?) {
        listener = onDialogClickListener
    }

}