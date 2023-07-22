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

class HomeworkMessageDialog(val context: Context, val screenPos:Int, val title :String,val list: List<HomeworkMessage.MessageBean>) {

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
        recyclerview.addItemDecoration(SpaceItemDeco(0,0,0,15))

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

    fun setData(datas: List<HomeworkMessage.MessageBean>){
        mAdapter?.setNewData(datas)
    }

    class MessageAdapter(layoutResId: Int, data: List<HomeworkMessage.MessageBean>) : BaseQuickAdapter<HomeworkMessage.MessageBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkMessage.MessageBean) {
            helper.setText(R.id.tv_title,item.title)
            if (item.endTime>0){
                helper.setText(R.id.tv_date, DateUtils.longToStringWeek(item.endTime*1000))
                val state=if (item.status==3){
                    mContext.getString(R.string.homework_state_no)
                }
                else if (item.status==1){
                    mContext.getString(R.string.homework_state_yes)
                }
                else{
                    mContext.getString(R.string.homework_state_complete)
                }
                helper.setText(R.id.tv_state,state)
            }
        }

    }

}