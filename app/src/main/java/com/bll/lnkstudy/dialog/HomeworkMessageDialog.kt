package com.bll.lnkstudy.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkMessageDialog(val context: Context, val screenPos:Int, val title :String,val list: List<HomeworkMessage>) {

    private var dialog:Dialog?=null
    private var mAdapter:MessageAdapter?=null

    @SuppressLint("SetTextI18n")
    fun builder(): HomeworkMessageDialog? {
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_homework_message)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog?.window!!
        val layoutParams =window.attributes
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,600f))/2
        }
        dialog?.show()

        val tv_title = dialog!!.findViewById<TextView>(R.id.tv_title)
        tv_title.text=title+context.getString(R.string.homework_message)
        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)

        recyclerview.layoutManager = LinearLayoutManager(context)
        mAdapter= MessageAdapter(R.layout.item_homework_message_all, list)
        recyclerview.adapter = mAdapter
        recyclerview.addItemDecoration(SpaceItemDeco(0,0,0,10))

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

    fun setData(datas: List<HomeworkMessage>){
        mAdapter?.setNewData(datas)
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(position: Int,id:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

    class MessageAdapter(layoutResId: Int, data: List<HomeworkMessage>) : BaseQuickAdapter<HomeworkMessage, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkMessage) {
            helper.setText(R.id.tv_title,item.title)
            helper.setText(R.id.tv_date, DateUtils.longToStringWeek(item.date))
        }

    }

}