package com.bll.lnkstudy.dialog

import android.annotation.Nullable
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.HomeworkMessage
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkMessageSelectorDialog(val context: Context, val screenPos:Int, val messages: List<HomeworkMessage>) {

    private var dialog:Dialog?=null

    fun builder(): HomeworkMessageSelectorDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_homework_message)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog?.window
        val layoutParams =window?.attributes
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,460f))/2
        }
        window?.attributes = layoutParams
        dialog?.show()

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)

        recyclerview.layoutManager = LinearLayoutManager(context)
        val mAdapter= MessageAdapter(R.layout.item_homework_message_selector, messages)
        recyclerview.adapter = mAdapter
        recyclerview.addItemDecoration(SpaceItemDeco(0,0,0,10,0))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            for (item in messages)
            {
                item.isSelector=false
            }
            messages[position].isSelector=true
            mAdapter?.notifyItemChanged(position)
            dismiss()
            listener?.onClick(messages[position])
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
        fun onClick(item: HomeworkMessage)
    }

    fun setOnDialogClickListener(@Nullable listener: OnDialogClickListener){
        this.listener = listener
    }

    class MessageAdapter(layoutResId: Int, data: List<HomeworkMessage>) : BaseQuickAdapter<HomeworkMessage, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkMessage) {
            helper.setText(R.id.tv_title,item.title)
            helper.setChecked(R.id.cb_check,item.isSelector)
        }

    }

}